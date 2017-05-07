package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

public abstract class PrivateKey extends Key {

    private PublicKey publicKey;

    public abstract PublicKey getPublicKey();

    public abstract String decodeMessage(String message);

    /**
     * Encrypts the SHA256 hash of the message with this private key.
     * @param message the message to sign
     * @return a String representing the signature.
     */
    public abstract String signMessage(String message);
}
