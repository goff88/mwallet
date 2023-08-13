/** ****************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ***************************************************************************** */
package pro.devapp.mwallet.core.main;

import org.json.simple.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pro.devapp.mwallet.core.Convert;
import pro.devapp.mwallet.core.PrizmException;
import pro.devapp.mwallet.core.crypto.Crypto;
import pro.devapp.mwallet.core.http.HttpClientFactory;

public final class SignTransactionJSON {

    private static final String BASE_URL = "https://192.168.1.115:7699/prizm";

    private static final String SECRET_PHRASE = "magic warmth style message bowl shout wander power yeah peach hallway human special moon student practice magic warmth style message bowl shout wander power yeah peach hallway human special moon student practice"; // Only needed for signTransactionCall

    public static void main(String[] args) throws IOException {
        try {
            SignTransactionJSON signTransactionJSON = new SignTransactionJSON();
            JSONObject obj = signTransactionJSON.createTransaction(
                    SECRET_PHRASE,
                    "PRIZM-6TLW-U2GA-FSWY-4CEYG",
                    0.01
            );
            String json = signTransactionJSON.signTransaction(obj);
            signTransactionJSON.sendBroadcast(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Transaction.Builder newTransactionBuilder(JSONObject transactionJSON) throws PrizmException.NotValidException {
        return TransactionImpl.newTransactionBuilder(transactionJSON);
    }


    private JSONObject createTransaction(
            String passPhrase,
            String receiverAccount,
            Double amount
    ) throws IOException {

        byte[] publicKey = Crypto.getPublicKey(passPhrase);
        String pkey = Convert.toHexString(publicKey);

        HttpClientFactory clientFactory = new HttpClientFactory();

        RequestBody formBody = new FormBody.Builder()
                .add("requestType", "sendMoney")
                .add("recipient", receiverAccount)
                .add("amountNQT", (Double.valueOf(amount*100.00).intValue())+"")
                .add("publicKey", pkey)
                .add("deadline", "1144")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build();

        Call call = clientFactory.createClient().newCall(request);
        Response response = call.execute();

        String responseString = response.body().string();
        System.out.print(responseString);
        JO jo = JO.parse(responseString);

        System.out.println(jo.get("transactionJSON"));

        return (JSONObject) jo.get("transactionJSON");
    }


    private String signTransaction(JSONObject json) throws PrizmException.NotValidException {
        //            String reader = "{\"senderPublicKey\":\"174ca7a158515ee1401a4962c7569ef119ffc6e985fbd7466410129415ff881e\",\"feeNQT\":\"5\",\"type\":0,\"version\":1,\"ecBlockId\":\"10345477375087728429\",\"attachment\":{\"version.OrdinaryPayment\":0},\"senderRS\":\"PRIZM-QVRW-46X6-42L7-CGV3Q\",\"subtype\":0,\"amountNQT\":\"1\",\"sender\":\"17012226861877194813\",\"recipientRS\":\"PRIZM-6TLW-U2GA-FSWY-4CEYG\",\"recipient\":\"7601529546835520221\",\"ecBlockHeight\":2615745,\"deadline\":1144,\"timestamp\":157044530,\"height\":2147483647}";
//            JSONObject json = (JSONObject) JSONValue.parseWithException(reader);
  //      byte[] publicKeyHash = Crypto.sha256().digest(Convert.parseHexString((String) json.get("senderPublicKey")));
        // String senderRS = Convert.rsAccount(Convert.fullHashToId(publicKeyHash));
        String secretPhrase = SECRET_PHRASE;
        Transaction.Builder builder = newTransactionBuilder(json);
        Transaction transaction = builder.build(secretPhrase);
        System.out.println(transaction.getJSONObject().toJSONString());
        return transaction.getJSONObject().toJSONString();
    }

    private void sendBroadcast(
            String json
    ) throws IOException {


        HttpClientFactory clientFactory = new HttpClientFactory();

        RequestBody formBody = new FormBody.Builder()
                .add("requestType", "broadcastTransaction")
                .add("transactionJSON", json)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build();

        Call call = clientFactory.createClient().newCall(request);
        Response response = call.execute();

        String responseString = response.body().string();
        System.out.println(responseString);
    }
}
