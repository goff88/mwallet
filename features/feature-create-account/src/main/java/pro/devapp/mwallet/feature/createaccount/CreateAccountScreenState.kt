package pro.devapp.mwallet.feature.createaccount

internal data class CreateAccountScreenState(
    val account: String,
    val passPhrase: String,
    val publicKey: String
)