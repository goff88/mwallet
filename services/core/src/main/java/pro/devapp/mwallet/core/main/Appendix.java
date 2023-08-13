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

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;

import pro.devapp.mwallet.core.Constants;
import pro.devapp.mwallet.core.Convert;
import pro.devapp.mwallet.core.PrizmException;
import pro.devapp.mwallet.core.crypto.Crypto;
import pro.devapp.mwallet.core.crypto.EncryptedData;

public interface Appendix {

    int getSize();

    int getFullSize();

    void putBytes(ByteBuffer buffer);

    JSONObject getJSONObject();

    byte getVersion();

    int getBaselineFeeHeight();

    int getNextFeeHeight();

    interface Prunable {

        byte[] getHash();

        boolean hasPrunableData();

        void restorePrunableData(Transaction transaction, int blockTimestamp, int height);

        default boolean shouldLoadPrunable(Transaction transaction, boolean includeExpiredPrunable) {
            return 0 - transaction.getTimestamp() <
                    (includeExpiredPrunable && Constants.INCLUDE_EXPIRED_PRUNABLE ?
                            Constants.MAX_PRUNABLE_LIFETIME : Constants.MIN_PRUNABLE_LIFETIME);
        }
    }

    interface Encryptable {

        void encrypt(String secretPhrase);
    }

    abstract class AbstractAppendix implements Appendix {

        private final byte version;

        AbstractAppendix(JSONObject attachmentData) {
            Long l = (Long) attachmentData.get("version." + getAppendixName());
            version = (byte) (l == null ? 0 : l);
        }

        AbstractAppendix(ByteBuffer buffer, byte transactionVersion) {
            if (transactionVersion == 0) {
                version = 0;
            } else {
                version = buffer.get();
            }
        }

        AbstractAppendix(int version) {
            this.version = (byte) version;
        }

        AbstractAppendix() {
            this.version = 1;
        }

        abstract String getAppendixName();

        @Override
        public final int getSize() {
            return getMySize() + (version > 0 ? 1 : 0);
        }

        @Override
        public final int getFullSize() {
            return getMyFullSize() + (version > 0 ? 1 : 0);
        }

        abstract int getMySize();

        int getMyFullSize() {
            return getMySize();
        }

        @Override
        public final void putBytes(ByteBuffer buffer) {
            if (version > 0) {
                buffer.put(version);
            }
            putMyBytes(buffer);
        }

        abstract void putMyBytes(ByteBuffer buffer);

        @Override
        public final JSONObject getJSONObject() {
            JSONObject json = new JSONObject();
            json.put("version." + getAppendixName(), version);
            putMyJSON(json);
            return json;
        }

        abstract void putMyJSON(JSONObject json);

        @Override
        public final byte getVersion() {
            return version;
        }

        boolean verifyVersion(byte transactionVersion) {
            return transactionVersion == 0 ? version == 0 : version == 1;
        }

        @Override
        public int getBaselineFeeHeight() {
            return 0;
        }

        @Override
        public int getNextFeeHeight() {
            return Integer.MAX_VALUE;
        }

        abstract void validate(Transaction transaction) throws PrizmException.ValidationException;

        final void loadPrunable(Transaction transaction) {
            loadPrunable(transaction, false);
        }

        void loadPrunable(Transaction transaction, boolean includeExpiredPrunable) {
        }
    }

    static boolean hasAppendix(String appendixName, JSONObject attachmentData) {
        return attachmentData.get("version." + appendixName) != null;
    }

    class Message extends AbstractAppendix {

        private static final String appendixName = "Message";

        static Message parse(JSONObject attachmentData) {
            if (!hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            return new Message(attachmentData);
        }

        private final byte[] message;
        private final boolean isText;

        Message(ByteBuffer buffer, byte transactionVersion) throws PrizmException.NotValidException {
            super(buffer, transactionVersion);
            int messageLength = buffer.getInt();
            this.isText = messageLength < 0; // ugly hack
            if (messageLength < 0) {
                messageLength &= Integer.MAX_VALUE;
            }
            if (messageLength > 1000) {
                throw new PrizmException.NotValidException("Invalid arbitrary message length: " + messageLength);
            }
            this.message = new byte[messageLength];
            buffer.get(this.message);
            if (isText && !Arrays.equals(message, Convert.toBytes(Convert.toString(message)))) {
                throw new PrizmException.NotValidException("Message is not UTF-8 text");
            }
        }

        Message(JSONObject attachmentData) {
            super(attachmentData);
            String messageString = (String) attachmentData.get("message");
            this.isText = Boolean.TRUE.equals(attachmentData.get("messageIsText"));
            this.message = isText ? Convert.toBytes(messageString) : Convert.parseHexString(messageString);
        }

        public Message(byte[] message) {
            this(message, false);
        }

        public Message(String string) {
            this(Convert.toBytes(string), true);
        }

        public Message(String string, boolean isText) {
            this(isText ? Convert.toBytes(string) : Convert.parseHexString(string), isText);
        }

        public Message(byte[] message, boolean isText) {
            this.message = message;
            this.isText = isText;
        }

        @Override
        String getAppendixName() {
            return appendixName;
        }

        @Override
        int getMySize() {
            return 4 + message.length;
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.putInt(isText ? (message.length | Integer.MIN_VALUE) : message.length);
            buffer.put(message);
        }

        @Override
        void putMyJSON(JSONObject json) {
            json.put("message", Convert.toString(message, isText));
            json.put("messageIsText", isText);
        }


        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {
            if (message.length > Constants.MAX_ARBITRARY_MESSAGE_LENGTH) {
                throw new PrizmException.NotValidException("Invalid arbitrary message length: " + message.length);
            }
        }


        public byte[] getMessage() {
            return message;
        }

        public boolean isText() {
            return isText;
        }
    }

    class PrunablePlainMessage extends AbstractAppendix implements Prunable {

        private static final String appendixName = "PrunablePlainMessage";

        static PrunablePlainMessage parse(JSONObject attachmentData) {
            if (!hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            return new PrunablePlainMessage(attachmentData);
        }

        private final byte[] hash;
        private final byte[] message;
        private final boolean isText;

        PrunablePlainMessage(ByteBuffer buffer, byte transactionVersion) {
            super(buffer, transactionVersion);
            this.hash = new byte[32];
            buffer.get(this.hash);
            this.message = null;
            this.isText = false;
        }

        private PrunablePlainMessage(JSONObject attachmentData) {
            super(attachmentData);
            String hashString = Convert.emptyToNull((String) attachmentData.get("messageHash"));
            String messageString = Convert.emptyToNull((String) attachmentData.get("message"));
            if (hashString != null && messageString == null) {
                this.hash = Convert.parseHexString(hashString);
                this.message = null;
                this.isText = false;
            } else {
                this.hash = null;
                this.isText = Boolean.TRUE.equals(attachmentData.get("messageIsText"));
                this.message = Convert.toBytes(messageString, isText);
            }
        }

        public PrunablePlainMessage(byte[] message) {
            this(message, false);
        }

        public PrunablePlainMessage(String string) {
            this(Convert.toBytes(string), true);
        }

        public PrunablePlainMessage(String string, boolean isText) {
            this(Convert.toBytes(string, isText), isText);
        }

        public PrunablePlainMessage(byte[] message, boolean isText) {
            this.message = message;
            this.isText = isText;
            this.hash = null;
        }

        @Override
        String getAppendixName() {
            return appendixName;
        }

        @Override
        int getMySize() {
            return 32;
        }

        @Override
        int getMyFullSize() {
            return getMessage() == null ? 0 : getMessage().length;
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.put(getHash());
        }

        @Override
        void putMyJSON(JSONObject json) {

        }

        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {
            if (transaction.getMessage() != null) {
                throw new PrizmException.NotValidException("Cannot have both message and prunable message attachments");
            }
            byte[] msg = getMessage();
            if (msg != null && msg.length > Constants.MAX_PRUNABLE_MESSAGE_LENGTH) {
                throw new PrizmException.NotValidException("Invalid prunable message length: " + msg.length);
            }
            if (msg == null && 0 - transaction.getTimestamp() < Constants.MIN_PRUNABLE_LIFETIME) {
                throw new PrizmException.NotCurrentlyValidException("Message has been pruned prematurely");
            }
        }



        public byte[] getMessage() {
            return message;
        }

        public boolean isText() {
            return isText;
        }

        @Override
        public byte[] getHash() {
            if (hash != null) {
                return hash;
            }
            MessageDigest digest = Crypto.sha256();
            digest.update((byte) (isText ? 1 : 0));
            digest.update(message);
            return digest.digest();
        }

        @Override
        final void loadPrunable(Transaction transaction, boolean includeExpiredPrunable) {

        }

        @Override
        public final boolean hasPrunableData() {
            return ( message != null);
        }

        @Override
        public void restorePrunableData(Transaction transaction, int blockTimestamp, int height) {

        }
    }

    abstract class AbstractEncryptedMessage extends AbstractAppendix {

        private EncryptedData encryptedData;
        private final boolean isText;
        private final boolean isCompressed;

        private AbstractEncryptedMessage(ByteBuffer buffer, byte transactionVersion) throws PrizmException.NotValidException {
            super(buffer, transactionVersion);
            int length = buffer.getInt();
            this.isText = length < 0;
            if (length < 0) {
                length &= Integer.MAX_VALUE;
            }
            this.encryptedData = EncryptedData.readEncryptedData(buffer, length, 1000);
            this.isCompressed = getVersion() != 2;
        }

        private AbstractEncryptedMessage(JSONObject attachmentJSON, JSONObject encryptedMessageJSON) {
            super(attachmentJSON);
            byte[] data = Convert.parseHexString((String) encryptedMessageJSON.get("data"));
            byte[] nonce = Convert.parseHexString((String) encryptedMessageJSON.get("nonce"));
            this.encryptedData = new EncryptedData(data, nonce);
            this.isText = Boolean.TRUE.equals(encryptedMessageJSON.get("isText"));
            Object isCompressed = encryptedMessageJSON.get("isCompressed");
            this.isCompressed = isCompressed == null || Boolean.TRUE.equals(isCompressed);
        }

        private AbstractEncryptedMessage(EncryptedData encryptedData, boolean isText, boolean isCompressed) {
            super(isCompressed ? 1 : 2);
            this.encryptedData = encryptedData;
            this.isText = isText;
            this.isCompressed = isCompressed;
        }

        @Override
        int getMySize() {
            return 4 + encryptedData.getSize();
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.putInt(isText ? (encryptedData.getData().length | Integer.MIN_VALUE) : encryptedData.getData().length);
            buffer.put(encryptedData.getData());
            buffer.put(encryptedData.getNonce());
        }

        @Override
        void putMyJSON(JSONObject json) {
            json.put("data", Convert.toHexString(encryptedData.getData()));
            json.put("nonce", Convert.toHexString(encryptedData.getNonce()));
            json.put("isText", isText);
            json.put("isCompressed", isCompressed);
        }


        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {
//ss            if (Prizm.getBlockchain().getHeight() > Constants.SHUFFLING_BLOCK && getEncryptedDataLength() > Constants.MAX_ENCRYPTED_MESSAGE_LENGTH) {

        }

        @Override
        final boolean verifyVersion(byte transactionVersion) {
            return transactionVersion == 0 ? getVersion() == 0 : (getVersion() == 1 || getVersion() == 2);
        }


        public final EncryptedData getEncryptedData() {
            return encryptedData;
        }

        final void setEncryptedData(EncryptedData encryptedData) {
            this.encryptedData = encryptedData;
        }

        int getEncryptedDataLength() {
            return encryptedData.getData().length;
        }

        public final boolean isText() {
            return isText;
        }

        public final boolean isCompressed() {
            return isCompressed;
        }
    }

    class PrunableEncryptedMessage extends AbstractAppendix implements Prunable {

        private static final String appendixName = "PrunableEncryptedMessage";


        static PrunableEncryptedMessage parse(JSONObject attachmentData) {
            if (!hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            JSONObject encryptedMessageJSON = (JSONObject) attachmentData.get("encryptedMessage");
            if (encryptedMessageJSON != null && encryptedMessageJSON.get("data") == null) {
                return new UnencryptedPrunableEncryptedMessage(attachmentData);
            }
            return new PrunableEncryptedMessage(attachmentData);
        }

        private final byte[] hash;
        private EncryptedData encryptedData;
        private final boolean isText;
        private final boolean isCompressed;

        PrunableEncryptedMessage(ByteBuffer buffer, byte transactionVersion) {
            super(buffer, transactionVersion);
            this.hash = new byte[32];
            buffer.get(this.hash);
            this.encryptedData = null;
            this.isText = false;
            this.isCompressed = false;
        }

        private PrunableEncryptedMessage(JSONObject attachmentJSON) {
            super(attachmentJSON);
            String hashString = Convert.emptyToNull((String) attachmentJSON.get("encryptedMessageHash"));
            JSONObject encryptedMessageJSON = (JSONObject) attachmentJSON.get("encryptedMessage");
            if (hashString != null && encryptedMessageJSON == null) {
                this.hash = Convert.parseHexString(hashString);
                this.encryptedData = null;
                this.isText = false;
                this.isCompressed = false;
            } else {
                this.hash = null;
                byte[] data = Convert.parseHexString((String) encryptedMessageJSON.get("data"));
                byte[] nonce = Convert.parseHexString((String) encryptedMessageJSON.get("nonce"));
                this.encryptedData = new EncryptedData(data, nonce);
                this.isText = Boolean.TRUE.equals(encryptedMessageJSON.get("isText"));
                this.isCompressed = Boolean.TRUE.equals(encryptedMessageJSON.get("isCompressed"));
            }
        }

        public PrunableEncryptedMessage(EncryptedData encryptedData, boolean isText, boolean isCompressed) {
            this.encryptedData = encryptedData;
            this.isText = isText;
            this.isCompressed = isCompressed;
            this.hash = null;
        }

        @Override
        final int getMySize() {
            return 32;
        }

        @Override
        final int getMyFullSize() {
            return getEncryptedDataLength();
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.put(getHash());
        }

        @Override
        void putMyJSON(JSONObject json) {
            if (encryptedData != null) {
                JSONObject encryptedMessageJSON = new JSONObject();
                json.put("encryptedMessage", encryptedMessageJSON);
                encryptedMessageJSON.put("data", Convert.toHexString(encryptedData.getData()));
                encryptedMessageJSON.put("nonce", Convert.toHexString(encryptedData.getNonce()));
                encryptedMessageJSON.put("isText", isText);
                encryptedMessageJSON.put("isCompressed", isCompressed);
            }
            json.put("encryptedMessageHash", Convert.toHexString(getHash()));
        }

        @Override
        final String getAppendixName() {
            return appendixName;
        }

        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {
            if (transaction.getEncryptedMessage() != null) {
                throw new PrizmException.NotValidException("Cannot have both encrypted and prunable encrypted message attachments");
            }
            EncryptedData ed = getEncryptedData();
            if (ed == null && 0 - transaction.getTimestamp() < Constants.MIN_PRUNABLE_LIFETIME) {
                throw new PrizmException.NotCurrentlyValidException("Encrypted message has been pruned prematurely");
            }
            if (ed != null) {
                if (ed.getData().length > Constants.MAX_PRUNABLE_ENCRYPTED_MESSAGE_LENGTH) {
                    throw new PrizmException.NotValidException(String.format("Message length %d exceeds max prunable encrypted message length %d",
                            ed.getData().length, Constants.MAX_PRUNABLE_ENCRYPTED_MESSAGE_LENGTH));
                }
                if ((ed.getNonce().length != 32 && ed.getData().length > 0)
                        || (ed.getNonce().length != 0 && ed.getData().length == 0)) {
                    throw new PrizmException.NotValidException("Invalid nonce length " + ed.getNonce().length);
                }
            }
            if (transaction.getRecipientId() == 0) {
                throw new PrizmException.NotValidException("Encrypted messages cannot be attached to transactions with no recipient");
            }
        }

        public final EncryptedData getEncryptedData() {
            return encryptedData;
        }

        final void setEncryptedData(EncryptedData encryptedData) {
            this.encryptedData = encryptedData;
        }

        int getEncryptedDataLength() {
            return getEncryptedData() == null ? 0 : getEncryptedData().getData().length;
        }

        public final boolean isText() {
            return isText;
        }

        public final boolean isCompressed() {
            return isCompressed;
        }

        @Override
        public final byte[] getHash() {
            if (hash != null) {
                return hash;
            }
            MessageDigest digest = Crypto.sha256();
            digest.update((byte) (isText ? 1 : 0));
            digest.update((byte) (isCompressed ? 1 : 0));
            digest.update(encryptedData.getData());
            digest.update(encryptedData.getNonce());
            return digest.digest();
        }

        @Override
        void loadPrunable(Transaction transaction, boolean includeExpiredPrunable) {

        }

        @Override
        public final boolean hasPrunableData() {
            return ( encryptedData != null);
        }

        @Override
        public void restorePrunableData(Transaction transaction, int blockTimestamp, int height) {

        }
    }

    final class UnencryptedPrunableEncryptedMessage extends PrunableEncryptedMessage implements Encryptable {

        private final byte[] messageToEncrypt;
        private final byte[] recipientPublicKey;

        private UnencryptedPrunableEncryptedMessage(JSONObject attachmentJSON) {
            super(attachmentJSON);
            setEncryptedData(null);
            JSONObject encryptedMessageJSON = (JSONObject) attachmentJSON.get("encryptedMessage");
            String messageToEncryptString = (String) encryptedMessageJSON.get("messageToEncrypt");
            this.messageToEncrypt = isText() ? Convert.toBytes(messageToEncryptString) : Convert.parseHexString(messageToEncryptString);
            this.recipientPublicKey = Convert.parseHexString((String) attachmentJSON.get("recipientPublicKey"));
        }

        public UnencryptedPrunableEncryptedMessage(byte[] messageToEncrypt, boolean isText, boolean isCompressed, byte[] recipientPublicKey) {
            super(null, isText, isCompressed);
            this.messageToEncrypt = messageToEncrypt;
            this.recipientPublicKey = recipientPublicKey;
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            if (getEncryptedData() == null) {
                throw new PrizmException.NotYetEncryptedException("Prunable encrypted message not yet encrypted");
            }
            super.putMyBytes(buffer);
        }

        @Override
        void putMyJSON(JSONObject json) {
            if (getEncryptedData() == null) {
                JSONObject encryptedMessageJSON = new JSONObject();
                encryptedMessageJSON.put("messageToEncrypt", isText() ? Convert.toString(messageToEncrypt) : Convert.toHexString(messageToEncrypt));
                encryptedMessageJSON.put("isText", isText());
                encryptedMessageJSON.put("isCompressed", isCompressed());
                json.put("recipientPublicKey", Convert.toHexString(recipientPublicKey));
                json.put("encryptedMessage", encryptedMessageJSON);
            } else {
                super.putMyJSON(json);
            }
        }

        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {
            if (getEncryptedData() == null) {
                int dataLength = getEncryptedDataLength();
                if (dataLength > Constants.MAX_PRUNABLE_ENCRYPTED_MESSAGE_LENGTH) {
                    throw new PrizmException.NotValidException(String.format("Message length %d exceeds max prunable encrypted message length %d",
                            dataLength, Constants.MAX_PRUNABLE_ENCRYPTED_MESSAGE_LENGTH));
                }
            } else {
                super.validate(transaction);
            }
        }

        @Override
        void loadPrunable(Transaction transaction, boolean includeExpiredPrunable) {
        }

        @Override
        public void encrypt(String secretPhrase) {
            setEncryptedData(EncryptedData.encrypt(getPlaintext(), secretPhrase, recipientPublicKey));
        }

        @Override
        int getEncryptedDataLength() {
            return EncryptedData.getEncryptedDataLength(getPlaintext());
        }

        private byte[] getPlaintext() {
            return isCompressed() && messageToEncrypt.length > 0 ? Convert.compress(messageToEncrypt) : messageToEncrypt;
        }

    }

    class EncryptedMessage extends AbstractEncryptedMessage {

        private static final String appendixName = "EncryptedMessage";

        static EncryptedMessage parse(JSONObject attachmentData) {
            if (!hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            if (((JSONObject) attachmentData.get("encryptedMessage")).get("data") == null) {
                return new UnencryptedEncryptedMessage(attachmentData);
            }
            return new EncryptedMessage(attachmentData);
        }

        EncryptedMessage(ByteBuffer buffer, byte transactionVersion) throws PrizmException.NotValidException {
            super(buffer, transactionVersion);
        }

        EncryptedMessage(JSONObject attachmentData) {
            super(attachmentData, (JSONObject) attachmentData.get("encryptedMessage"));
        }

        public EncryptedMessage(EncryptedData encryptedData, boolean isText, boolean isCompressed) {
            super(encryptedData, isText, isCompressed);
        }

        @Override
        final String getAppendixName() {
            return appendixName;
        }

        @Override
        void putMyJSON(JSONObject json) {
            JSONObject encryptedMessageJSON = new JSONObject();
            super.putMyJSON(encryptedMessageJSON);
            json.put("encryptedMessage", encryptedMessageJSON);
        }

        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {
            super.validate(transaction);
            if (transaction.getRecipientId() == 0) {
                throw new PrizmException.NotValidException("Encrypted messages cannot be attached to transactions with no recipient");
            }
        }

    }

    final class UnencryptedEncryptedMessage extends EncryptedMessage implements Encryptable {

        private final byte[] messageToEncrypt;
        private final byte[] recipientPublicKey;

        UnencryptedEncryptedMessage(JSONObject attachmentData) {
            super(attachmentData);
            setEncryptedData(null);
            JSONObject encryptedMessageJSON = (JSONObject) attachmentData.get("encryptedMessage");
            String messageToEncryptString = (String) encryptedMessageJSON.get("messageToEncrypt");
            messageToEncrypt = isText() ? Convert.toBytes(messageToEncryptString) : Convert.parseHexString(messageToEncryptString);
            recipientPublicKey = Convert.parseHexString((String) attachmentData.get("recipientPublicKey"));
        }

        public UnencryptedEncryptedMessage(byte[] messageToEncrypt, boolean isText, boolean isCompressed, byte[] recipientPublicKey) {
            super(null, isText, isCompressed);
            this.messageToEncrypt = messageToEncrypt;
            this.recipientPublicKey = recipientPublicKey;
        }

        @Override
        int getMySize() {
            if (getEncryptedData() != null) {
                return super.getMySize();
            }
            return 4 + EncryptedData.getEncryptedSize(getPlaintext());
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            if (getEncryptedData() == null) {
                throw new PrizmException.NotYetEncryptedException("Message not yet encrypted");
            }
            super.putMyBytes(buffer);
        }

        @Override
        void putMyJSON(JSONObject json) {
            if (getEncryptedData() == null) {
                JSONObject encryptedMessageJSON = new JSONObject();
                encryptedMessageJSON.put("messageToEncrypt", isText() ? Convert.toString(messageToEncrypt) : Convert.toHexString(messageToEncrypt));
                encryptedMessageJSON.put("isText", isText());
                encryptedMessageJSON.put("isCompressed", isCompressed());
                json.put("encryptedMessage", encryptedMessageJSON);
                json.put("recipientPublicKey", Convert.toHexString(recipientPublicKey));
            } else {
                super.putMyJSON(json);
            }
        }

        @Override
        public void encrypt(String secretPhrase) {
            setEncryptedData(EncryptedData.encrypt(getPlaintext(), secretPhrase, recipientPublicKey));
        }

        private byte[] getPlaintext() {
            return isCompressed() && messageToEncrypt.length > 0 ? Convert.compress(messageToEncrypt) : messageToEncrypt;
        }

        @Override
        int getEncryptedDataLength() {
            return EncryptedData.getEncryptedDataLength(getPlaintext());
        }

    }

    class EncryptToSelfMessage extends AbstractEncryptedMessage {

        private static final String appendixName = "EncryptToSelfMessage";

        static EncryptToSelfMessage parse(JSONObject attachmentData) {
            if (!hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            if (((JSONObject) attachmentData.get("encryptToSelfMessage")).get("data") == null) {
                return new UnencryptedEncryptToSelfMessage(attachmentData);
            }
            return new EncryptToSelfMessage(attachmentData);
        }

        EncryptToSelfMessage(ByteBuffer buffer, byte transactionVersion) throws PrizmException.NotValidException {
            super(buffer, transactionVersion);
        }

        EncryptToSelfMessage(JSONObject attachmentData) {
            super(attachmentData, (JSONObject) attachmentData.get("encryptToSelfMessage"));
        }

        public EncryptToSelfMessage(EncryptedData encryptedData, boolean isText, boolean isCompressed) {
            super(encryptedData, isText, isCompressed);
        }

        @Override
        final String getAppendixName() {
            return appendixName;
        }

        @Override
        void putMyJSON(JSONObject json) {
            JSONObject encryptToSelfMessageJSON = new JSONObject();
            super.putMyJSON(encryptToSelfMessageJSON);
            json.put("encryptToSelfMessage", encryptToSelfMessageJSON);
        }

    }

    final class UnencryptedEncryptToSelfMessage extends EncryptToSelfMessage implements Encryptable {

        private final byte[] messageToEncrypt;

        UnencryptedEncryptToSelfMessage(JSONObject attachmentData) {
            super(attachmentData);
            setEncryptedData(null);
            JSONObject encryptedMessageJSON = (JSONObject) attachmentData.get("encryptToSelfMessage");
            String messageToEncryptString = (String) encryptedMessageJSON.get("messageToEncrypt");
            messageToEncrypt = isText() ? Convert.toBytes(messageToEncryptString) : Convert.parseHexString(messageToEncryptString);
        }

        public UnencryptedEncryptToSelfMessage(byte[] messageToEncrypt, boolean isText, boolean isCompressed) {
            super(null, isText, isCompressed);
            this.messageToEncrypt = messageToEncrypt;
        }

        @Override
        int getMySize() {
            if (getEncryptedData() != null) {
                return super.getMySize();
            }
            return 4 + EncryptedData.getEncryptedSize(getPlaintext());
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            if (getEncryptedData() == null) {
                throw new PrizmException.NotYetEncryptedException("Message not yet encrypted");
            }
            super.putMyBytes(buffer);
        }

        @Override
        void putMyJSON(JSONObject json) {
            if (getEncryptedData() == null) {
                JSONObject encryptedMessageJSON = new JSONObject();
                encryptedMessageJSON.put("messageToEncrypt", isText() ? Convert.toString(messageToEncrypt) : Convert.toHexString(messageToEncrypt));
                encryptedMessageJSON.put("isText", isText());
                encryptedMessageJSON.put("isCompressed", isCompressed());
                json.put("encryptToSelfMessage", encryptedMessageJSON);
            } else {
                super.putMyJSON(json);
            }
        }


        @Override
        public void encrypt(String secretPhrase) {
            setEncryptedData(EncryptedData.encrypt(getPlaintext(), secretPhrase, Crypto.getPublicKey(secretPhrase)));
        }

        @Override
        int getEncryptedDataLength() {
            return EncryptedData.getEncryptedDataLength(getPlaintext());
        }

        private byte[] getPlaintext() {
            return isCompressed() && messageToEncrypt.length > 0 ? Convert.compress(messageToEncrypt) : messageToEncrypt;
        }

    }

    final class PublicKeyAnnouncement extends AbstractAppendix {

        private static final String appendixName = "PublicKeyAnnouncement";

        static PublicKeyAnnouncement parse(JSONObject attachmentData) {
            if (!hasAppendix(appendixName, attachmentData)) {
                return null;
            }
            return new PublicKeyAnnouncement(attachmentData);
        }

        private final byte[] publicKey;

        PublicKeyAnnouncement(ByteBuffer buffer, byte transactionVersion) {
            super(buffer, transactionVersion);
            this.publicKey = new byte[32];
            buffer.get(this.publicKey);
        }

        PublicKeyAnnouncement(JSONObject attachmentData) {
            super(attachmentData);
            this.publicKey = Convert.parseHexString((String) attachmentData.get("recipientPublicKey"));
        }

        public PublicKeyAnnouncement(byte[] publicKey) {
            this.publicKey = publicKey;
        }

        @Override
        String getAppendixName() {
            return appendixName;
        }

        @Override
        int getMySize() {
            return 32;
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.put(publicKey);
        }

        @Override
        void putMyJSON(JSONObject json) {
            json.put("recipientPublicKey", Convert.toHexString(publicKey));
        }

        @Override
        void validate(Transaction transaction) throws PrizmException.ValidationException {

        }


        public byte[] getPublicKey() {
            return publicKey;
        }

    }
}
