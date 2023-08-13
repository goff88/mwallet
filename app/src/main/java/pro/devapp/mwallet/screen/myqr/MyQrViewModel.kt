package pro.devapp.mwallet.screen.myqr

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pro.devapp.mwallet.core.CoreAPI
import pro.devapp.mwallet.data.AccountInMemoryRepository


class MyQrViewModel(
    private val coreAPI: CoreAPI,
    private val accountInMemoryRepository: AccountInMemoryRepository
) : ViewModel() {

    private val _screenState = MutableStateFlow(
        MyQrState()
    )
    val screenState: StateFlow<MyQrState>
        get() = _screenState

    fun init() {
        viewModelScope.launch {
            val encodedImage = coreAPI.getQr(
                address = accountInMemoryRepository.accountId?.address ?: "",
                pkey = accountInMemoryRepository.accountId?.publicKey ?: ""
            )
            val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            _screenState.emit(
                _screenState.value.copy(
                    img = decodedByte
                )
            )
        }
    }
}