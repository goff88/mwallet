package pro.devapp.mwallet.feature.createaccount

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat

internal class ClipBoard(
    private val context: Context
) {

    fun copy(text: String) {
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("Password phrase", text)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "Password phrase copied", Toast.LENGTH_LONG).show()
    }
}