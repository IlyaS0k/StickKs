package ru.ilyasok.StickKs.feature.telegram.entities

import ru.ilyasok.StickKs.tdapi.TdApi

data class SenderInfo(
    val id: Long = 0,
    val name: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
)

fun TdApi.User.toSenderInfo(): SenderInfo {
    return SenderInfo(
        id = this.id,
        name = this.usernames.editableUsername,
        firstName = this.firstName,
        lastName = this.lastName,
        phoneNumber = this.phoneNumber,
    )
}