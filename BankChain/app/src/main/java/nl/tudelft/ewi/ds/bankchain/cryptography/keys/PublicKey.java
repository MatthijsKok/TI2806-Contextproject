package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

/**
 * The PublicKey class.
 * Public keys verify the signature of any message signed by its private key.
 * Also can encrypt any message, so that only the private key can decrypt it.
 */
public abstract class PublicKey extends Key {

    /**
     * Encrypt a message with this public key.
     * This encrypted message will be able to be decrypted by the corresponding private key.
     * @param message the message String to encrypt.
     * @return The encrypted message String.
     */
    public abstract String encryptMessage(String message);

    /**
     * Verifies the message with the signature.
     * Decrypts the signature with this public key, and compares that to the SHA256 of the message.
     * @param message the message to verify.
     * @return Whether the two SHA256 hashes match.
     */
    public abstract boolean verifySignedMessage(String message, String signature);
}
