package pro.devapp.mwallet.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.data.AccountInMemoryRepository

internal class WalletViewModel(
    private val coreAPI: pro.devapp.mwallet.core.CoreAPI,
    private val cmcAPI: CmcAPI,
    private val accountInMemoryRepository: AccountInMemoryRepository
) : ViewModel() {

    private val walletScreenState = MutableStateFlow(WalletScreenState("", 0.0, 0.0))
    val screenState: StateFlow<WalletScreenState>
        get() = walletScreenState.asStateFlow()

    fun onInit() {
        viewModelScope.launch {
            accountInMemoryRepository.accountId?.let {
                walletScreenState.value = WalletScreenState(
                    it.address,
                    0.0,
                    0.0
                )
                val balance = coreAPI.getAccountBalance(it.address)
                val balanceRub = cmcAPI.calculateBalance(balance)
                walletScreenState.value = walletScreenState.value.copy(
                    balance = balance,
                    balanceRUB = balanceRub
                )
            }
        }
    }
}