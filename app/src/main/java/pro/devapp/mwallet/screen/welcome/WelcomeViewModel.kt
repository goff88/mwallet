package pro.devapp.mwallet.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.data.PassPhraseManager
import pro.devapp.mwallet.navigation.NavigationAction

class WelcomeViewModel(
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
        accountInMemoryRepository.accountId = result
        accountInMemoryRepository.passPhrase = phrase
        _navigationFlow.emit(NavigationAction.Wallet)
    }
}