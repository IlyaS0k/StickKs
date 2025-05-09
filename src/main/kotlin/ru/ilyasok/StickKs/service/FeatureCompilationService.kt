package ru.ilyasok.StickKs.service

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.compilerRunner.toArgumentStrings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.dsl.DSLDependenciesProvider
import ru.ilyasok.StickKs.dsl.FeatureBlock
import ru.ilyasok.StickKs.model.Feature
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader
import kotlin.text.replaceFirst

@Service
class FeatureCompilationService {

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

    fun compile(featureCode: String): CompilationResult = synchronized(this) {
        try {
            val source = imports
                .plus("\n")
                .plus(
                    featureCode.replaceFirst(
                        Regex("""feature\s*\{"""), "fun $COMPILED_METHOD_NAME() = feature {"
                    )
                )

            val sourceFile = File(compilationOutputDir, "Feature.kt").apply { writeText(source) }
            val bootClasses = File(UNPACKED_BOOT_CLASSES)
            val bootLibs = File(UNPACKED_BOOT_LIBS).listFiles()!!.toList()
            val args = K2JVMCompilerArguments().apply {
                freeArgs = listOf(sourceFile.absolutePath)
                destination = compilationOutputDir.absolutePath
                classpath = (listOf(bootClasses) + bootLibs).joinToString(File.pathSeparator) { it.absolutePath }
            }

            K2JVMCompiler().exec(System.err, *args.toArgumentStrings().toTypedArray())

            val classLoader = URLClassLoader(arrayOf(compilationOutputDir.toURI().toURL()))
            val clazz = classLoader.loadClass("FeatureKt")
            val method = clazz.getDeclaredMethod(COMPILED_METHOD_NAME)
            val feature = method.invoke(null) as FeatureBlock
            logger.info("Successfully compiled feature\n: $featureCode")
            return CompilationResult(success = true, featureBlock = feature)
        } catch (error: InvocationTargetException) {
            val e = error.targetException
            logger.info("Incorrect DSL syntax\n: $featureCode", e)
            return CompilationResult(success = false, error = e)
        } catch (e: Throwable) {
            logger.info("Error while compiling feature\n: $featureCode", e)
            return CompilationResult(success = false, error = e)
        }
    }

}