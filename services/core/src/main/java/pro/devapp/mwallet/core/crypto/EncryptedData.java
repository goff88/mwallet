/******************************************************************************
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
 ******************************************************************************/

package pro.devapp.mwallet.core.crypto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pro.devapp.mwallet.core.Convert;
import pro.devapp.mwallet.core.PrizmException;

public final class EncryptedData {

    public static final EncryptedData EMPTY_DATA = new EncryptedData(new byte[0], new byte[0]);

    public static EncryptedData encrypt(byte[] plaintext, String secretPhrase, byte[] theirPublicKey) {
        if (plaintext.length == 0) {
            return EMPTY_DATA;
        }
        byte[] nonce = new byte[32];
        Crypto.getSecureRandom().nextBytes(nonce);
        byte[] sharedKey = Crypto.getSharedKey(Crypto.getPrivateKey(secretPhrase), theirPublicKey, nonce);
        byte[] data = Crypto.aesEncrypt(plaintext, sharedKey);
        return new EncryptedData(data, nonce);
    }

    public static EncryptedData readEncryptedData(ByteBuffer buffer, int length, int maxLength)
            throws PrizmException.NotValidException {
        if (length == 0) {
            return EMPTY_DATA;
        }
        if (length > maxLength) {
            throw new PrizmException.NotValidException("Max encrypted data length exceeded: " + length);
        }
        byte[] data = new byte[length];
        buffer.get(data);
        byte[] nonce = new byte[32];
        buffer.get(nonce);
        return new EncryptedData(data, nonce);
    }

    public static EncryptedData readEncryptedData(byte[] bytes) {
        if (bytes.length == 0) {
            return EMPTY_DATA;
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        try {
            return readEncryptedData(buffer, bytes.length - 32, Integer.MAX_VALUE);
        } catch (PrizmException.NotValidException e) {
            throw new RuntimeException(e.toString(), e); // never
        }
    }

    public static int getEncryptedDataLength(byte[] plaintext) {
        if (plaintext.length == 0) {
            return 0;
        }
        return Crypto.aesEncrypt(plaintext, new byte[32]).length;
    }

    public static int getEncryptedSize(byte[] plaintext) {
        if (plaintext.length == 0) {
            return 0;
        }
        return getEncryptedDataLength(plaintext) + 32;
    }

    private final byte[] data;
    private final byte[] nonce;

    public EncryptedData(byte[] data, byte[] nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public byte[] decrypt(String secretPhrase, byte[] theirPublicKey) {
        if (data.length == 0) {
            return data;
        }
        byte[] sharedKey = Crypto.getSharedKey(Crypto.getPrivateKey(secretPhrase), theirPublicKey, nonce);
        return Crypto.aesDecrypt(data, sharedKey);
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public int getSize() {
        return data.length + nonce.length;
    }

    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(nonce.length + data.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(data);
        buffer.put(nonce);
        return buffer.array();
    }

    @Override
    public String toString() {
        return "data: " + Convert.toHexString(data) + " nonce: " + Convert.toHexString(nonce);
    }

}
