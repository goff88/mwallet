package pro.devapp.mwallet.screen.sendmoney

data class SendMoneyScreenState(
    val recipientAccount: String,
    val needPublicKey: Boolean,
    val publicKey: String,
    val recipientAccountBalance: Double?,
    val message: String,
    val amount: String
)