package nl.tudelft.ewi.ds.bankchain.bank;

import android.support.annotation.Nullable;


import java.util.List;

import java8.util.concurrent.CompletableFuture;


public abstract class Bank {

    // public abstract boolean isSandbox();

    /**
     * Create a new session.
     */
    public abstract CompletableFuture<? extends Session> createSession();

    /**
     * Destroy current session
     * @return completable
     */
    public abstract CompletableFuture<Void> destroySession();

    public abstract CompletableFuture<List<? extends Transaction>> listTransactions();
//
//    public abstract CompletableFuture<Boolean> sendTransaction(Transaction transaction);

    /**
     * Get the current session of the bank.
     *
     * @return current session or null;
     */
    @Nullable
    public abstract Session getCurrentSession();

    public abstract Throwable confirmException(Throwable e);
}
