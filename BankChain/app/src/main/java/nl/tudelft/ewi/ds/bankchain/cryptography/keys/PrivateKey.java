package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

public abstract class PrivateKey extends Key {

    abstract PublicKey getPublicKey();

    abstract String decodeMessage(String message);

    abstract String signMessage(String message);
}
