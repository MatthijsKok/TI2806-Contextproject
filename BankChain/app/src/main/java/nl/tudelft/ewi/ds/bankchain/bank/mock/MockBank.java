package nl.tudelft.ewi.ds.bankchain.bank.mock;

import android.support.annotation.Nullable;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Session;

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
