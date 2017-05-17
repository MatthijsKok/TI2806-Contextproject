package nl.tudelft.ewi.ds.bankchain.bank.mock;

import android.support.annotation.Nullable;


import java.util.List;


import java8.util.concurrent.CompletableFuture;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;

/**
 * Created by Richard-HP on 09/05/2017.
 */

public class MockBank extends Bank {

    public MockBank(){

    }


    @Override
    public CompletableFuture<Session> createSession() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<? extends Transaction>> listTransactions(Account account) {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Party>> listUsers() {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends Account>> listAccount(Party party) {
        return null;
    }

    @Nullable
    @Override
    public Session getCurrentSession() {
        return null;
    }

    @Override
    public Throwable confirmException(Throwable e) {
        return e;
    }
}
