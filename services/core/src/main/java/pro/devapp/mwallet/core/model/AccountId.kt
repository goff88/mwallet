package pro.devapp.mwallet.core.model

data class AccountId(
    val id: Long,
    val publicKey: String,
    val address: String
)
