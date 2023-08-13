package pro.devapp.mwallet.data

private const val PIN_KEY = "pin"

class PinManager(
    private val encryptedStorage: EncryptedStorage
) {

    suspend fun hasPin(): Boolean {
        return encryptedStorage.get(PIN_KEY)?.isNotEmpty() ?: false
    }

    suspend fun resetPin() {
        encryptedStorage.save(PIN_KEY, "")
    }

    suspend fun savePin(pin: String) {
        // TODO add hashing
        encryptedStorage.save(PIN_KEY, pin)
    }

    suspend fun checkPin(pin: String): Boolean {
        val savedPin = encryptedStorage.get(PIN_KEY)
        return savedPin == pin
    }
}