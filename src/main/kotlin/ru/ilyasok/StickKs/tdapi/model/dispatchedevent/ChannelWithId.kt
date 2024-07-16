package ru.ilyasok.StickKs.tdapi.model.dispatchedevent

import kotlinx.coroutines.channels.Channel

data class ChannelWithId<T> (
    val id: Long,
    val channel: Channel<T>
)