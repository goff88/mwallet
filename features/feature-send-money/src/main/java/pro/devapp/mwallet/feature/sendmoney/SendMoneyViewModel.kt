package pro.devapp.mwallet.feature.sendmoney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.core.CoreAPI
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.navigation.NavigationAction

//PRIZM-TE8N-B3VM-JJQH-5NYJB - Genesis
class SendMoneyViewModel(
    private val coreAPI: CoreAPI,
    private val accountInMemoryRepository: AccountInMemoryRepository
) : ViewModel() {

    private val _navigationFlow = MutableSharedFlow<NavigationAction>()
    val navigationFlow: SharedFlow<NavigationAction>
        get() = _navigationFlow.asSharedFlow()

    private val _screenState = MutableStateFlow(
        SendMoneyScreenState("", false, "", null, "", "")
    )
    val screenState: StateFlow<SendMoneyScreenState>
        get() = _screenState

    fun onRecipientAccountChanged(account: String) {
        viewModelScope.launch {
            _screenState.emit(
                _screenState.value.copy(
                    recipientAccount = account
                )
            )
            if (account.length == "PRIZM-6TLW-U2GA-FSWY-4CEYG".length) {
                loadRecipientInfo(account)
            } else if (_screenState.value.needPublicKey) {
                _screenState.emit(
                    _screenState.value.copy(
                        needPublicKey = false,
                        recipientAccountBalance = null,
                        publicKey = ""
                    )
                )
            }
        }
    }

    fun onValueChanged(amount: String) {
        viewModelScope.launch {
            _screenState.emit(
                _screenState.value.copy(
                    amount = amount.replace(",", ".")
                )
            )
        }
    }

    fun sendMoney() {
        viewModelScope.launch {
            _screenState.value.amount.toDoubleOrNull()?.let {
                val rawTransaction = coreAPI.createTransaction(
                    _screenState.value.recipientAccount,
                    accountInMemoryRepository.passPhrase!!,
                    (it * 100).toLong()
                )
                val signedTransaction =
                    coreAPI.signTransaction(rawTransaction, accountInMemoryRepository.passPhrase!!)
                coreAPI.sendBroadcast(signedTransaction!!)
                _navigationFlow.emit(NavigationAction.Wallet)
            }
        }
    }

    private fun loadRecipientInfo(recipientAddress: String) {
        viewModelScope.launch {
            val result = coreAPI.getAccountDetails(recipientAddress)
            if (result == null) {
                _screenState.emit(
                    _screenState.value.copy(
                        needPublicKey = true,
                        recipientAccountBalance = null
                    )
                )
            } else {
                _screenState.emit(
                    _screenState.value.copy(
                        needPublicKey = false,
                        recipientAccountBalance = result.balanceNQT.toDouble() / 100,
                        publicKey = result.publicKey
                    )
                )
            }
        }
    }

    fun handleQrResult(result: String?) {
        result ?: return
        viewModelScope.launch {
            try {
                val dataArray = result.split(":")
                _screenState.emit(
                    _screenState.value.copy(
                        recipientAccount = dataArray[0],
                        publicKey = dataArray[1]
                    )
                )
                loadRecipientInfo(result)
            } catch (e: Exception) {

            }
        }
    }
}