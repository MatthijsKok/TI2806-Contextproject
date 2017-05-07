package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

public abstract class PublicKey extends Key {

    public abstract String encryptMessage(String message);

    public abstract boolean verifySignedMessage(String message);
}
