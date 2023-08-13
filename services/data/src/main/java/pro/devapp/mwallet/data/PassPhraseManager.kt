package pro.devapp.mwallet.data

private const val PASS_PHRASE_KEY = "pass_phrase"

class PassPhraseManager(
    private val encryptedStorage: EncryptedStorage
) {

    suspend fun hasPhrase(): Boolean {
        return encryptedStorage.get(PASS_PHRASE_KEY)?.isNotEmpty() ?: false
    }

    suspend fun resetPhrase() {
        encryptedStorage.save(PASS_PHRASE_KEY, "")
    }

    suspend fun savePhrase(phrase: String) {
        encryptedStorage.save(PASS_PHRASE_KEY, phrase)
    }

    suspend fun getPhrase(): String {
        return encryptedStorage.get(PASS_PHRASE_KEY) ?: ""
    }
}