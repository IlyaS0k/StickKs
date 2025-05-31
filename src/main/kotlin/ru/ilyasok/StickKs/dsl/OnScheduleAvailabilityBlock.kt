package ru.ilyasok.StickKs.dsl

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import org.springframework.scheduling.support.CronExpression
import ru.ilyasok.StickKs.core.utils.SpringContext
import java.time.ZonedDateTime

class OnScheduleAvailabilityBlock(
    val cron: String,
    val limit: Long
) : ExecutionControlBlock() {
    override fun control(meta: FeatureMeta): Boolean {
        if (meta.successExecutionsAmount >= limit) return false
        val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)
        val parser = CronParser(cronDefinition)
        val cron = parser.parse(cron)
        val executionTime = ExecutionTime.forCron(cron)
        val now = ZonedDateTime.now().withNano(0)

        return executionTime.isMatch(now)
    }
}

class OnScheduleAvailabilityBlockBuilder {
    var cron: String? = null
    var limit: Long = Long.MAX_VALUE

    companion object {
        private val cronRegex = Regex("""^(\S+\s){4}\S+$""")
    }

    fun build() : OnScheduleAvailabilityBlock {
        assert(cron != null) { "Cron is not specified" }
        assert(cronRegex.matches(cron!!)) { "Invalid cron" }

        return OnScheduleAvailabilityBlock(cron = cron!!, limit = limit)
    }
}

@FeatureDSL
fun FeatureBlockBuilder<*>.schedule(block: OnScheduleAvailabilityBlockBuilder.() -> Unit) : OnScheduleAvailabilityBlock {
    val ab = OnScheduleAvailabilityBlockBuilder().apply(block).build()
    this.executionControl = ab
    return ab
}