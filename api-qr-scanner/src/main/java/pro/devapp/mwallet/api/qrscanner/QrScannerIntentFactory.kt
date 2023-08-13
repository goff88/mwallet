package pro.devapp.mwallet.api.qrscanner

import android.content.Context
import android.content.Intent

interface QrScannerIntentFactory {

    fun createIntent(context: Context): Intent
}