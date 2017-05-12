package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

/**
 * The PrivateKey class.
 * Private keys allow to sign a message verifiable by the public key.
 * Also allows to decrypt any message encrypted by the public key.
 */
public abstract class PrivateKey extends Key {

    /**
     * The public key that belongs to this private key.
     */
    @SuppressWarnings("unused")
    private PublicKey publicKey;

    /**
     * Calculates the public key corresponding to this private key and stores it.
     * @return The public key that belongs to this private key.
     */
    public abstract PublicKey getPublicKey();

    /**
     * Decode a message encrypted with the public key that belongs to this private key.
     * @param message The encrypted message.
     * @return The decrypted message.
     */
    public abstract String decodeMessage(String message);

    /**
     * Encrypts the SHA256 hash of the message with this private key.
     * @param message the message to sign
     * @return a String representing the signature.
     */
    public abstract String signMessage(String message);
}
