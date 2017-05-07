package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

public abstract class PublicKey extends Key {

    public abstract String encryptMessage(String message);

    /**
     * Verifies the message with the signature.
     * Decrypts the signature with this public key, and compares that to the SHA256 of the message.
     * @param message the message to verify.
     * @return Whether the two SHA256 hashes match.
     */
    public abstract boolean verifySignedMessage(String message, String signature);
}
