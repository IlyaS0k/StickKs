package ru.ilyasok.StickKs.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.compilerRunner.toArgumentStrings
import org.jetbrains.kotlin.utils.PathUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.dsl.DSLDependenciesProvider
import ru.ilyasok.StickKs.dsl.FeatureBlock
import ru.ilyasok.StickKs.repository.IFeatureRepository
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader
import java.util.UUID

@Service
class FeatureCompilationService(
    private val featureErrorsService: FeatureErrorsService,
    private val featureRepository: IFeatureRepository,
) {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(FeatureCompilationService::class.java)

        private const val COMPILED_METHOD_NAME = "getFeature"

        private const val OUTPUT_DIR_NAME = "feature-compilation-output"

        private const val UNPACKED_BOOT_CLASSES = "./BOOT-INF/classes"

        private const val UNPACKED_BOOT_LIBS = "./BOOT-INF/lib"

    }

    data class CompilationResult(
        val success: Boolean,
        val featureBlock: FeatureBlock? = null,
        val error: Throwable? = null
    ) {
        init {
            require(if (success) featureBlock != null && error == null else featureBlock == null && error != null)
        }
    }

    private var compilationOutputDir: File = File(OUTPUT_DIR_NAME).apply { mkdirs() }

    private val imports = DSLDependenciesProvider.provideAsString()

    fun compileAsync(id: UUID?, featureCode: String) = CoroutineScope(Dispatchers.IO).async {
        val isBroken = if (id != null) featureRepository.findById(id)?.isBroken else false
        if (isBroken != true) {
            return@async compile(id, featureCode)
        }
        return@async CompilationResult(success = false, error = RuntimeException("attempt to compile broken feature"), featureBlock = null)
    }

    fun compile(id: UUID?, featureCode: String): CompilationResult {
        val threadId = Thread.currentThread().id
        val compilationOutputFile = "Feature${threadId}.kt"
        val classToLoad = "Feature${threadId}Kt"
        val nameSubstrToDelete = "Feature${threadId}"
        try {
            val source = imports
                .plus("\n")
                .plus(
                    featureCode.replaceFirst(
                        Regex("""feature\s*\{"""), "fun $COMPILED_METHOD_NAME() = feature {"
                    )
                )
            val additionalPath = PathUtil.getJdkClassesRootsFromCurrentJre()
            val sourceFile = File(compilationOutputDir, compilationOutputFile).apply { writeText(source) }
            val bootClasses = File(UNPACKED_BOOT_CLASSES)
            val bootLibs = File(UNPACKED_BOOT_LIBS).listFiles()?.toList() ?: emptyList()
            val args = K2JVMCompilerArguments().apply {
                freeArgs = listOf(sourceFile.absolutePath)
                destination = compilationOutputDir.absolutePath
                classpath = (listOf(bootClasses) + bootLibs + additionalPath).joinToString(File.pathSeparator) { it.absolutePath } + File.pathSeparator + System.getProperty("java.class.path")
            }
            val compilationOutputStream = ByteArrayOutputStream()
            val exitCode = K2JVMCompiler().exec(PrintStream(compilationOutputStream), *args.toArgumentStrings().toTypedArray())
            if (exitCode != ExitCode.OK) {
                val compilationError = RuntimeException("$exitCode : $compilationOutputStream")
                if (id != null) runBlocking {
                    setBroken(id)
                    featureErrorsService.updateFeatureErrors(id, compilationError.stackTraceToString())
                }
                return CompilationResult(success = false, error = compilationError)
            }
            val classLoader = URLClassLoader(arrayOf(compilationOutputDir.toURI().toURL()))
            val clazz = classLoader.loadClass(classToLoad)
            val method = clazz.getDeclaredMethod(COMPILED_METHOD_NAME)
            val feature = method.invoke(null) as FeatureBlock
            logger.debug("Successfully compiled feature\n: $featureCode")
            return CompilationResult(success = true, featureBlock = feature)
        } catch (error: InvocationTargetException) {
            val e = error.targetException
            logger.info("Incorrect DSL syntax\n: $featureCode", e)
            return CompilationResult(success = false, error = e)
        } catch (e: Throwable) {
            logger.info("Error while compiling feature\n: $featureCode", e)
            return CompilationResult(success = false, error = e)
        } finally {
            compilationOutputDir.listFiles()?.forEach { if (it.name.contains(nameSubstrToDelete)) it.delete() }
        }
    }


    private suspend fun setBroken(featureId: UUID)  {
        try {
            val feature = featureRepository.findById(featureId) ?: throw RuntimeException("Feature with id $featureId not found")
            optimisticTry(10) {
                featureRepository.save(feature.copy(isBroken = true))
            }
        } catch (e: Throwable) {
            logger.error("Error while setting broken feature with if\n: $featureId", e)
        }
    }

}