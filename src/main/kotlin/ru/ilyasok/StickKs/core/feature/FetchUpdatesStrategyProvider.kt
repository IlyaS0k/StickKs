package ru.ilyasok.StickKs.core.feature

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.service.FeatureErrorsService
import ru.ilyasok.StickKs.service.NotificationService

@Component
class FetchUpdatesStrategyProvider(
    @param:Value("\${stickks.features.management.fetch-updates-strategy}")
    private val fetchUpdatesStrategy: String,
    private val strategies: List<IFetchUpdatesStrategy>
) {

    fun provide(): IFetchUpdatesStrategy  {
        return strategies.firstOrNull { strategy -> strategy.name() == fetchUpdatesStrategy } ?:
            throw Error("Unknown fetch updates strategy: $fetchUpdatesStrategy")
    }
}