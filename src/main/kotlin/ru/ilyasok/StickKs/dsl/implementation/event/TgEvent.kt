package ru.ilyasok.StickKs.dsl.implementation.event

import ru.ilyasok.StickKs.tdapi.TdApi

class TgEvent(val type: TdApi.Object) : Event()
