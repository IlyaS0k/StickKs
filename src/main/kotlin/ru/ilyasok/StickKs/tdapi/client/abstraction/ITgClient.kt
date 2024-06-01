package ru.ilyasok.StickKs.tdapi.client.abstraction

import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQueryHandler
import ru.ilyasok.StickKs.tdapi.model.TdQueryHandlerResponse

interface ITgClient {
     val adapteeClient: Client
     val authorizationState: TgClientAuthorizationState
     val mainHandler: ITdMainHandler
     val tgClientParams: TgClientParams
     fun send(query: TdApi.Function<*>)
     suspend fun <R, E> sendWithCallback(
          query: TdApi.Function<*>, queryHandler: ITdQueryHandler<R, E>
     ) : TdQueryHandlerResponse<R, E>
     suspend fun sendWithCallback(
          query: TdApi.Function<*>
     ): TdQueryHandlerResponse<TdApi.Object, TdApi.Error>

}