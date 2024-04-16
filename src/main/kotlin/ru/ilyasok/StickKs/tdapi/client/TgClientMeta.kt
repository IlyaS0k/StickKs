package ru.ilyasok.StickKs.tdapi.client


data class TgClientMeta(
    val useTestDc: Boolean?,
    val databaseDirectory: String?,
    val filesDirectory: String?,
    val databaseEncryptionKey: String?,
    val useFileDatabase: Boolean?,
    val useChatInfoDatabase: Boolean?,
    val useMessageDatabase: Boolean?,
    val useSecretChats: Boolean?,
    val apiId: Int?,
    val apiHash: String?,
    val phone: String?,
)
