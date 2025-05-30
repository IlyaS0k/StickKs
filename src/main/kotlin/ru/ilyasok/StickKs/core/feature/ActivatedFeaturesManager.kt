package ru.ilyasok.StickKs.core.feature

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.core.event.source.IEventSource
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.dsl.OnScheduleAvailabilityBlock
import ru.ilyasok.StickKs.dsl.WithTimePeriodAvailabilityBlock
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.model.OperationResult
import ru.ilyasok.StickKs.service.FeatureErrorsService
import ru.ilyasok.StickKs.service.NotificationService
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

data class ActivateFeatureEvent(val featureId: UUID) : EventContext()

@Component
class ActivatedFeaturesManager(
    private val notificationService: NotificationService,
    private val featureErrorsService: FeatureErrorsService,
) : IEventSource {

    @Autowired
    @Lazy
    private lateinit var self: ActivatedFeaturesManager

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private data class AppliedFeatureJob(
        val featureId: UUID,
        val job: Job,
    )

    private val currentApplied: MutableList<AppliedFeatureJob> = mutableListOf()
    private val mutex: Mutex = Mutex()

    private fun activate(featureId: UUID) = self.publishEvent(ActivateFeatureEvent(featureId))

    class ApplyActivatedFeatureResult(
        success: Boolean,
        error: Throwable? = null,
    ) : OperationResult(success, error)

    private fun cancelIfApplied(featureId: UUID) {
        currentApplied.firstOrNull { it.featureId == featureId }?.let {
            it.job.cancel()
            currentApplied.remove(it)
        }
    }

    suspend fun cancel(featureId: UUID) = mutex.withLock {
        cancelIfApplied(featureId)
    }

    suspend fun apply(feature: Feature): ApplyActivatedFeatureResult = mutex.withLock {
        return try {
            cancelIfApplied(feature.id)
            if (!feature.isActivated()) {
                logger.error("Failed to apply non-activated feature ${feature.idName()}")
                return ApplyActivatedFeatureResult(false)
            }
            val executionControl = feature.feature.executionControl::class

            when (executionControl) {
                WithTimePeriodAvailabilityBlock::class -> {
                    applyWithTimePeriod(feature)
                }

                OnScheduleAvailabilityBlock::class -> {
                    applyScheduled(feature)
                }

                else -> {
                    logger.error("Cannot apply feature ${feature.idName()} with execution control = $executionControl")
                    return ApplyActivatedFeatureResult(false)
                }
            }
            ApplyActivatedFeatureResult(true)
        } catch (t: Throwable) {
            logger.error("Failed to apply activated feature ${feature.idName()}", t)
            ApplyActivatedFeatureResult(false, t)
        }
    }

    private suspend fun applyWithTimePeriod(feature: Feature) {
        applyProxy(feature) {
            val activateId = feature.id
            val withTimePeriodBlock = feature.feature.executionControl as WithTimePeriodAvailabilityBlock
            delay(withTimePeriodBlock.afterStart)
            activate(activateId)

            while (true) {
                delay(withTimePeriodBlock.period)
                activate(activateId)
            }
        }
    }

    private suspend fun applyProxy(feature: Feature, block: suspend () -> Unit) = try {
        val job = CoroutineScope(Dispatchers.Default).launch {
            block()
        }
        currentApplied.add(AppliedFeatureJob(feature.id, job))
    } catch (_: CancellationException) {
        logger.debug("Activated feature {} was cancelled", feature.idName())
    } catch (e: Throwable) {
        notificationService.notify(feature.id, null, NotificationType.FEATURE_UNSTABLE)
        featureErrorsService.updateFeatureErrors(feature.id, e.stackTraceToString())
    }

    private suspend fun applyScheduled(feature: Feature) {
        applyProxy(feature) {
            val activateId = feature.id
            val scheduledBlock = feature.feature.executionControl as OnScheduleAvailabilityBlock
            val cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)
            val parser = CronParser(cronDef)
            val cron = parser.parse(scheduledBlock.cron)
            val executionTime = ExecutionTime.forCron(cron)
            while (true) {
                val now = ZonedDateTime.now(ZoneId.systemDefault())
                val nextExecution = executionTime.nextExecution(now)

                val delayMillis = nextExecution
                    .map { java.time.Duration.between(now, it).toMillis() }
                    .orElse(0L)
                delay(delayMillis)
                activate(activateId)
            }
        }
    }

}