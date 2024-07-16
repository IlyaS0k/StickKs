package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient

interface ITdHandler<T> {
     fun handle(client: ITgClient, obj: T)
}
