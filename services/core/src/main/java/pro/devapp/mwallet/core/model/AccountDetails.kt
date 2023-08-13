package pro.devapp.mwallet.core.model

data class AccountDetails(
    val balanceNQT: Long,
    val publicKey: String,
    val accountRS: String
)
