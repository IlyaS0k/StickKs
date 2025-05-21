package ru.ilyasok.StickKs.core.event

import ru.ilyasok.StickKs.core.context.EventContext
import java.util.UUID

data class TimerEvent(val featureId: UUID) : EventContext()