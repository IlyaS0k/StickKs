package ru.ilyasok.StickKs.feature.telegram.entities

import ru.ilyasok.StickKs.dsl.FeatureDSL
import ru.ilyasok.StickKs.tdapi.TdApi

data class TgUserIdentification(
    val phone: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
) {

    fun isSuitableUser(user: TdApi.User): Boolean {
        if (this.phone != null && user.phoneNumber != this.phone) return false
        if (this.username != null && user.usernames?.editableUsername != this.username) return false
        if (this.firstName != null && user.firstName != this.firstName) return false
        if (this.lastName != null && user.lastName != this.lastName) return false

        return true
    }
}

@FeatureDSL
class TgUserIdentificationBuilder {
    var phone: String? = null
    var username: String? = null
    var firstName: String? = null
    var lastName: String? = null

    fun build(): TgUserIdentification {
        assert(phone != null || username != null || firstName != null || lastName != null) {
            "Failed to create user: phone or username or firstName or lastName must be specified"
        }
        return TgUserIdentification(
            phone = phone,
            username = username,
            firstName = firstName,
            lastName = lastName
        )
    }
}

@FeatureDSL
fun user(block: TgUserIdentificationBuilder.() -> Unit): TgUserIdentification {
    return TgUserIdentificationBuilder().apply { block() }.build()
}