package nl.tudelft.ewi.ds.bankchain;

import android.content.Context;
import android.util.Log;

import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqAccount;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqParty;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTransaction;
import nl.tudelft.ewi.ds.bankchain.blockchain.Blockchain;
import nl.tudelft.ewi.ds.bankchain.cryptography.ED25519;

public class BankTeller {

    Context context;
    Blockchain blockchain;
    Environment env;
    Bank bank;

    private BankTeller(Context context, Environment env) {
        this.env = env;
        this.context = context;
        bank = new BankFactory(env, context).create();
        blockchain = new Blockchain("chain", context, true);
        bank.createSession();
    }

    public void sendCent(String iban, String name, String description) {
        Transaction t = new BunqTransaction(0.01f, getBasicaccount(), createRecipient(iban, name), Currency.getInstance("EUR"), description);
        bank.sendTransaction(t);
    }

    public void addkey(String name, String iban, String publicKey, boolean validated) {
        blockchain.addKey(ED25519.getPublicKey(publicKey), iban, name, validated);
        blockchain.save();
    }

    public List<Transaction> getTransactions() {
        try {
            return bank.listTransactions(getBasicaccount()).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("No transactions found", "Something went wrong!");
        }
        return null;
    }

    private Account createRecipient(String iban, String name) {
        Party p = new BunqParty(name, -1);
        return new BunqAccount(iban, -1, p);
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    private Account getBasicaccount() {
        Account ac = null;
        try {
            Party user = bank.listUsers().get().get(0);
            ac = bank.listAccount(user).get().get(0);
        } catch (InterruptedException e) {
            Log.e("Bankteller", e.toString());
        } catch (ExecutionException e) {
            Log.e("Bankteller", e.toString());
        }
        return ac;
    }


    private static BankTeller teller = null;

    public static BankTeller getBankTeller(Context context, Environment v) {
        if (teller == null) {
            teller = new BankTeller(context, v);
        }

        return teller;
    }

    public static BankTeller getBankTeller() {
        if (teller == null) {
            throw new RuntimeException("Initialize BankTeller before using");
        }

        return teller;
    }

}