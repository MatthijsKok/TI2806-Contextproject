package nl.tudelft.ewi.ds.bankchain.cryptography.keys;

public abstract class PublicKey extends Key {

    abstract String encryptMessage(String message);

    abstract boolean verifySignedMessage(String message);
}
