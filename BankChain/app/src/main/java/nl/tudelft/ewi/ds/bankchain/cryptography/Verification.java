package nl.tudelft.ewi.ds.bankchain.cryptography;


import java.security.PublicKey;

import nl.tudelft.ewi.ds.bankchain.bank.Account;

public class Verification {

    private Account account;
    private PublicKey publicKey;


    public Verification(Account account, PublicKey publicKey) {
        this.account = account;
        this.publicKey = publicKey;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Verification) {
            Verification that = (Verification) obj;
            return this.publicKey.equals(that.publicKey) && this.account.equals(that.account);
        }
        return false;
    }

    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }
}
