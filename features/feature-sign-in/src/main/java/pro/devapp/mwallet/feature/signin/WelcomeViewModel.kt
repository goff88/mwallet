package pro.devapp.mwallet.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.data.AccountData
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.data.PassPhraseManager
import pro.devapp.mwallet.navigation.NavigationAction

internal class WelcomeViewModel(
    private val coreAPI: pro.devapp.mwallet.core.CoreAPI,
    private val accountInMemoryRepository: AccountInMemoryRepository,
    private val passPhraseManager: PassPhraseManager
) : ViewModel() {

    private val _navigationFlow = MutableSharedFlow<NavigationAction>()
    val navigationFlow: SharedFlow<NavigationAction>
        get() = _navigationFlow.asSharedFlow()

    fun init() {
        viewModelScope.launch {
            if (passPhraseManager.hasPhrase()) {
                loadAccountInfo(passPhraseManager.getPhrase())
            }
        }
    }

    private suspend fun loadAccountInfo(phrase: String) {
        val result = coreAPI.getAccountId(phrase)
        accountInMemoryRepository.accountId = AccountData(
            id = result.id,
            publicKey = result.publicKey,
            address = result.address
        )
        accountInMemoryRepository.passPhrase = phrase
        _navigationFlow.emit(NavigationAction.Wallet)
    }
}