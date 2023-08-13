package pro.devapp.mwallet.feature.pinpad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.data.PinManager
import pro.devapp.mwallet.navigation.NavigationAction

class PinPadViewModel(
    private val pinManager: PinManager
) : ViewModel() {

    private val _screenState = MutableStateFlow(
        PinPadState()
    )

    val screenState: StateFlow<PinPadState>
        get() = _screenState

    private val _navigationFlow = MutableSharedFlow<NavigationAction>()
    val navigationFlow: SharedFlow<NavigationAction>
        get() = _navigationFlow.asSharedFlow()

    fun init() {
        viewModelScope.launch {
            val hasPin = pinManager.hasPin()
            if (hasPin) {
                _screenState.emit(_screenState.value.copy(title = "Enter PIN"))
            } else {
                _screenState.emit(
                    _screenState.value.copy(
                        title = "Create PIN",
                        isNewPin = true
                    )
                )
            }
        }
    }

    fun onPinChanged(pin: String) {
        viewModelScope.launch {
            val currentPin = _screenState.value.pin
            if (pin == "Del") {
                _screenState.value = _screenState.value.copy(pin = currentPin.dropLast(1))
            } else {
                if (currentPin.length >= 8) return@launch
                _screenState.value = _screenState.value.copy(pin = currentPin + pin)
            }
            if (_screenState.value.pin.length >= 8) {
                if (_screenState.value.isNewPin) {
                    pinManager.savePin(_screenState.value.pin)
                    _screenState.value = _screenState.value.copy(
                        pin = "",
                        title = "Enter PIN",
                        isNewPin = false
                    )
                } else {
                    val isPinCorrect = pinManager.checkPin(_screenState.value.pin)
                    if (isPinCorrect) {
                        _navigationFlow.emit(NavigationAction.Welcome)
                    } else {
                        _screenState.value = _screenState.value.copy(
                            pin = "",
                            title = "Enter PIN",
                            error = "Incorrect PIN"
                        )
                    }
                }
            }
        }
    }

}