package nl.tudelft.ewi.ds.bankchain.Blockchain;

import java.security.PublicKey;

/**
 * Created by Richard-HP on 01/06/2017.
 */

public class Block {

    String name;
    String iban;
    PublicKey publicKey;
    Boolean validated;

    public Block(String name, String iban, PublicKey publicKey, Boolean validated) {
        this.name = name;
        this.iban = iban;
        this.publicKey = publicKey;
        this.validated = validated;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

}
