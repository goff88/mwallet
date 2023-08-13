package pro.devapp.mwallet.feature.pinpad

internal data class PinPadState(
    val pin: String = "",
    val title: String = "",
    val error: String? = null,
    val isNewPin: Boolean = false
)