package ru.ilyasok.StickKs.dsl

import org.springframework.scheduling.support.CronExpression
import ru.ilyasok.StickKs.core.utils.SpringContext

class OnScheduleAvailabilityBlock(
    private val cron: String,
    private val limit: Long
) : ExecutionControlBlock() {
    override fun control(meta: FeatureMeta): Boolean {
        val clock = SpringContext.clock()
        if (meta.successExecutionsAmount >= limit) return false
        val expression = CronExpression.parse(cron)
        TODO()
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

fun FeatureBlockBuilder.schedule(block: OnScheduleAvailabilityBlockBuilder.() -> Unit) : OnScheduleAvailabilityBlock {
    val ab = OnScheduleAvailabilityBlockBuilder().apply(block).build()
    this.executionControl = ab
    return ab
}