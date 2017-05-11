package nl.tudelft.ewi.ds.bankchain.bank.mock;

import android.support.annotation.Nullable;


import java.util.List;


import java8.util.concurrent.CompletableFuture;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.TransactionAbstract;

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
    public List<Transaction> listTransactions(Session session) {
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
