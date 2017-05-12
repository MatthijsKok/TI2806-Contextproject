package nl.tudelft.ewi.ds.bankchain.bank;

import android.support.annotation.Nullable;


import java.util.List;

import java8.util.concurrent.CompletableFuture;


public abstract class Bank {

    // public abstract boolean isSandbox();

    /**
     * Create a new session.
     *
     * TODO: currently not returned. Need to see how it turns out with futures etc.
     */
    public abstract CompletableFuture<? extends Session> createSession();

    public abstract CompletableFuture<List<? extends Transaction>> listTransactions();
//
//    public abstract CompletableFuture<Boolean> sendTransaction(Transaction transaction);

    /**
     * Get the current session of the bank.
     *
     * TODO: it would, at the moment, be possible to switch sessions. For that it might be better to expose SessionStore.
     *
     * @return current session or null;
     */
    @Nullable
    public abstract Session getCurrentSession();

    public abstract Throwable confirmException(Throwable e);
}
