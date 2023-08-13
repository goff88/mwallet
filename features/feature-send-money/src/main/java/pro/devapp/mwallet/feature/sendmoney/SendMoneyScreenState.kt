package pro.devapp.mwallet.feature.sendmoney

data class SendMoneyScreenState(
    val recipientAccount: String,
    val needPublicKey: Boolean,
    val publicKey: String,
    val recipientAccountBalance: Double?,
    val message: String,
    val amount: String
)