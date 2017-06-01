package nl.tudelft.ewi.ds.bankchain.cryptography;


import java.security.PublicKey;

import nl.tudelft.ewi.ds.bankchain.bank.Account;

public class Verification {

    public Account account;
    public PublicKey publicKey;

    public Verification(Account account, PublicKey publicKey) {
        this.account = account;
        this.publicKey = publicKey;
    }
}
