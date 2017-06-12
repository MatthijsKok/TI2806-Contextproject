package nl.tudelft.ewi.ds.bankchain;

/**
 * @author Isha Dijcks
 */
public class VerifiedAccount {

    private String publicKey;
    private String iban;
    private String date;

    public VerifiedAccount(String publicKey, String iban, String dateString) {
        this.publicKey = publicKey;
        this.iban = iban;
        this.date = dateString;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return this.publicKey + ":" + this.iban + ":" + this.date;
    }
}
