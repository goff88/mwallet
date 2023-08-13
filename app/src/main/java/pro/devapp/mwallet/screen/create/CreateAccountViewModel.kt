package pro.devapp.mwallet.screen.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.components.ClipBoard
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.data.PassPhraseManager
import java.util.Random

class CreateAccountViewModel(
    private val coreAPI: pro.devapp.mwallet.core.CoreAPI,
    private val accountInMemoryRepository: AccountInMemoryRepository,
    private val clipBoard: ClipBoard,
    private val passPhraseManager: PassPhraseManager
): ViewModel() {

    private val screenStateFlow = MutableStateFlow(CreateAccountScreenState("", "", ""))
    val screenState: StateFlow<CreateAccountScreenState>
        get() = screenStateFlow.asStateFlow()

    fun onInit() {
        viewModelScope.launch {
            val password = createPassPhrase()
            val result = coreAPI.getAccountId(password)
            passPhraseManager.savePhrase(password)
            accountInMemoryRepository.accountId = result
            accountInMemoryRepository.passPhrase = password
            screenStateFlow.emit(
                CreateAccountScreenState(
                    result.address,
                    password,
                    result.publicKey
                )
            )
        }
    }

    fun onCopyClicked() {
        viewModelScope.launch {
            clipBoard.copy(screenState.value.passPhrase)
        }
    }

    private fun createPassPhrase(): String {
        val randomStrings = arrayOfNulls<String>(16)
        val random = Random()
        for (i in 0 until 16) {
            val word =
                CharArray(random.nextInt(8) + 3) // words of length 3 through 10. (1 and 2 letter words are boring.)
            for (j in word.indices) {
                word[j] = ('a' + random.nextInt(26))
            }
            randomStrings[i] = word.joinToString("")
        }
        return randomStrings.joinToString(" ")
    }

}