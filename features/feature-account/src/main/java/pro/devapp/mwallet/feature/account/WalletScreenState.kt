package pro.devapp.mwallet.feature.account

data class WalletScreenState(
    val account: String,
    val balance: Double,
    val balanceRUB: Double
)