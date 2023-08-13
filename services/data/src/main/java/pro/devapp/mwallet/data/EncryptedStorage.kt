package pro.devapp.mwallet.data

import android.content.Context
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import io.github.osipxd.security.crypto.createEncrypted
import kotlinx.coroutines.flow.firstOrNull

class EncryptedStorage(
    private val context: Context
) {

    private val dataStore = PreferenceDataStoreFactory.createEncrypted {
        EncryptedFile.Builder(
            // The file should have extension .preferences_pb
            context.dataStoreFile("w.preferences_pb"),
            context,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

    suspend fun save(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun get(key: String): String? {
        return dataStore.data.firstOrNull()?.get(stringPreferencesKey(key))
    }
}