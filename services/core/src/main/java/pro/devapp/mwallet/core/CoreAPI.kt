package pro.devapp.mwallet.core

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.simple.JSONObject
import pro.devapp.mwallet.core.crypto.Crypto
import pro.devapp.mwallet.core.http.HttpClientFactory
import pro.devapp.mwallet.core.main.JO
import pro.devapp.mwallet.core.main.SignTransactionJSON.newTransactionBuilder
import pro.devapp.mwallet.core.main.Transaction
import pro.devapp.mwallet.core.model.AccountDetails
import pro.devapp.mwallet.core.model.AccountId
import pro.devapp.mwallet.core.model.RawTransaction


private const val BASE_URL = "https://wallet.prizm.space/prizm"

//8dfb0a18-f1f7-407a-be14-484f06e17d3c

class CoreAPI {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun getAccountId(passPhrase: String): AccountId {
        return withContext(scope.coroutineContext) {
            val publicKey = Crypto.getPublicKey(passPhrase)
            val pkey = Convert.toHexString(publicKey)
            AccountId(
                id = getId(publicKey),
                publicKey = pkey,
                address = Convert.rsAccount(getId(publicKey))
            )
        }
    }

    suspend fun getAccountBalance(address: String): Double {
        return withContext(scope.coroutineContext) {

            val clientFactory = HttpClientFactory()

            val formBody: RequestBody = FormBody.Builder()
                .add("requestType", "getBalance")
                .add("account", address)
                .build()

            val request: Request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()
            print(responseString)
            val jo = JO.parse(responseString)
            Log.d("CoreAPI", jo.toString())
            Log.d("CoreAPI", jo.keys.toString())
            //account publicKey
            print(jo["balanceNQT"])
            jo["balanceNQT"].toString().toDouble() / 100.00
        }
    }

    suspend fun getAccountDetails(address: String): AccountDetails? {
        return withContext(scope.coroutineContext) {

            val clientFactory = HttpClientFactory()

            val formBody: RequestBody = FormBody.Builder()
                .add("requestType", "getAccount")
                .add("account", address)
                .build()

            val request: Request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()
            print(responseString)
            val jo = JO.parse(responseString)
            Log.d("CoreAPI", jo.toString())
            Log.d("CoreAPI", jo.keys.toString())
            //{"errorDescription":"Unknown account","accountRS":"PRIZM-SR89-HGBK-3D6C-BYDH2","errorCode":5,"account":"15154049369258100043"}
            if (jo["errorCode"] != null) {
                return@withContext null
            }
            AccountDetails(
                balanceNQT = jo["balanceNQT"].toString().toLong(),
                publicKey = jo["publicKey"].toString(),
                accountRS = jo["accountRS"].toString()
            )
        }
    }

    suspend fun getAccountInfo(pkey: String) {
        withContext(scope.coroutineContext) {

            val clientFactory = HttpClientFactory()

            val formBody: RequestBody = FormBody.Builder()
                .add("requestType", "getAccountId")
                .add("publicKey", pkey)
                .build()

            val request: Request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()
            print(responseString)
            val jo = JO.parse(responseString)
            Log.d("CoreAPI", jo.toString())
            Log.d("CoreAPI", jo.keys.toString())
            //account publicKey
            print(jo["accountRS"])
        }
    }

    suspend fun createTransaction(
        receiverAccount: String,
        passPhrase: String,
        amount: Long
    ): RawTransaction {
        return withContext(scope.coroutineContext) {

            val publicKey = Crypto.getPublicKey(passPhrase)
            val pkey = Convert.toHexString(publicKey)

            val clientFactory = HttpClientFactory()

            val formBody: RequestBody = FormBody.Builder()
                .add("requestType", "sendMoney")
                .add("recipient", receiverAccount)
                .add("amountNQT", amount.toString())
                .add("publicKey", pkey)
                .add("deadline", "1144")
                .build()

            val request: Request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()
            val jo = JO.parse(responseString)
            print(responseString)
            RawTransaction(jo["transactionJSON"] as JSONObject)
        }
    }

    suspend fun signTransaction(
        rawTransaction: RawTransaction,
        secretPhrase: String
    ): String? {
        return withContext(scope.coroutineContext) {
            val builder: Transaction.Builder = newTransactionBuilder(rawTransaction.jsonObject)
            val transaction: Transaction = builder.build(secretPhrase)
            transaction.getJSONObject().toJSONString()
        }
    }

    suspend fun sendBroadcast(
        json: String
    ) {
        return withContext(scope.coroutineContext) {

            val clientFactory = HttpClientFactory()

            val formBody: RequestBody = FormBody.Builder()
                .add("requestType", "broadcastTransaction")
                .add("transactionJSON", json)
                .build()

            val request: Request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()

            Log.d("CoreAPI", responseString)
//            val jo = JO.parse(responseString)
//            print(responseString)
//            jo["transactionJSON"] as JSONObject
        }
    }

    suspend fun getQr(address: String, pkey: String): String {
        return withContext(scope.coroutineContext) {

            val clientFactory = HttpClientFactory()

            val formBody: RequestBody = FormBody.Builder()
                .add("requestType", "encodeQRCode")
                .add("qrCodeData", "$address:$pkey")
                .add("width", "1200")
                .add("height", "1200")
                .build()

            val request: Request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()
            print(responseString)
            val jo = JO.parse(responseString)
            Log.d("CoreAPI", jo.toString())
            Log.d("CoreAPI", jo.keys.toString())
            //account publicKey
            print(jo["qrCodeBase64"])
            return@withContext jo["qrCodeBase64"].toString()
        }
    }

    private fun getId(publicKey: ByteArray): Long {
        val publicKeyHash = Crypto.sha256().digest(publicKey)
        return Convert.fullHashToId(publicKeyHash)
    }
}