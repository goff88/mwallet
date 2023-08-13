package pro.devapp.mwallet.feature.createaccount

data class CreateAccountScreenState(
    val account: String,
    val passPhrase: String,
    val publicKey: String
)