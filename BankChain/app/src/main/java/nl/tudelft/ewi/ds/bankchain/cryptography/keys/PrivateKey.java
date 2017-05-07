package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

public abstract class PrivateKey extends Key {

    private PublicKey publicKey;

    public abstract PublicKey getPublicKey();

    public abstract String decodeMessage(String message);

    public abstract String signMessage(String message);
}
