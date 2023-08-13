package pro.devapp.mwallet.screen.pin

data class PinPadState(
    val pin: String = "",
    val title: String = "",
    val error: String? = null,
    val isNewPin: Boolean = false
)
