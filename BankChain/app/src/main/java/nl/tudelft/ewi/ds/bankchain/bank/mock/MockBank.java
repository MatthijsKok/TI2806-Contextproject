package nl.tudelft.ewi.ds.bankchain.bank.mock;

import android.support.annotation.Nullable;


import java.util.List;


import java8.util.concurrent.CompletableFuture;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;

/**
 * Created by Richard-HP on 09/05/2017.
 */

public class MockBank extends Bank {

    public MockBank(){

    }


    @Override
    public CompletableFuture createSession() {
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
}
