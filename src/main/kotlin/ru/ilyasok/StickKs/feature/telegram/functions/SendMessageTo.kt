package ru.ilyasok.StickKs.feature.telegram.functions

import ru.ilyasok.StickKs.core.utils.SpringContext
import ru.ilyasok.StickKs.dsl.FeatureDSL
import ru.ilyasok.StickKs.feature.telegram.entities.TgUserIdentification

private fun processTgUserNameAndPhone(name: String?, phone: String?): Pair<String?, String?> {
    val phoneWithoutPlus = if (phone?.startsWith("+") == true) phone.substring(1) else phone
    val nameWithoutAt = if (name?.startsWith("@") == true) name.substring(1) else name
    return Pair(nameWithoutAt, phoneWithoutPlus)
}

@FeatureDSL
suspend fun sendMessageTo(
    phone: String? = null,
    username: String? = null,
    firstName: String? = null,
    lastName: String? = null,
    message: () -> String
) {
    assert(phone != null || username != null || firstName != null || lastName != null) { "All message receiver identification parameters is null" }
    val (nameWithoutAt, phoneWithoutPlus) = processTgUserNameAndPhone(username, phone)
    val tgFunctions = SpringContext.getBean(TgFunctions::class.java)

    tgFunctions.sendMessage(
        message = message.invoke(),
        TgUserIdentification(
            username = nameWithoutAt,
            phone = phoneWithoutPlus,
            firstName = firstName,
            lastName = lastName
        )
    )
}

@FeatureDSL
suspend fun sendMessageTo(user: TgUserIdentification, message: () -> String) {
    assert(user.phone != null || user.username != null || user.firstName != null || user.lastName != null) { "All message receiver identification parameters is null" }
    val (nameWithoutAt, phoneWithoutPlus) = processTgUserNameAndPhone(user.username, user.phone)
    val tgFunctions = SpringContext.getBean(TgFunctions::class.java)

    tgFunctions.sendMessage(
        message = message.invoke(),
        TgUserIdentification(
            username = nameWithoutAt,
            phone = phoneWithoutPlus,
            firstName = user.firstName,
            lastName = user.lastName
        )
    )
}

suspend fun TgFunctions.sendMessage(message: String, userIdentification: TgUserIdentification) {
    client.getContacts().handle {
        onSuccess = { it }
        onError = { e -> throw RuntimeException("Error while sending tg message: ${e.message}") }
    }
        ?.userIds
        ?.map { userId ->
            client.getUser(userId).handle {
                onSuccess = { it }
                onError = { null }
            }
        }
        ?.filterNotNull()
        ?.firstOrNull { user -> userIdentification.isSuitableUser(user) }
        ?.let { user -> client.sendMessage(user.id, message) }
}
