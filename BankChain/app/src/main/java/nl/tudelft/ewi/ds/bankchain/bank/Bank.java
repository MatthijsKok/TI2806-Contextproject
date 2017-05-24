package nl.tudelft.ewi.ds.bankchain.bank;

import android.support.annotation.Nullable;


import java.util.List;

import java8.util.concurrent.CompletableFuture;


public abstract class Bank {

    // public abstract boolean isSandbox();

    /**
     * Create a new session.
     * <p>
     * TODO: currently not returned. Need to see how it turns out with futures etc.
     */
    public abstract CompletableFuture<? extends Session> createSession();

    public abstract CompletableFuture<List<? extends Transaction>> listTransactions(Account account);

    public abstract CompletableFuture<List<? extends Party>> listUsers();

    public abstract CompletableFuture<List<Account>> listAccount(Party party);

    public abstract CompletableFuture<Boolean> sendTransaction(Transaction transaction);

    /**
     * Get the current session of the bank.
     *
     * @return current session or null;
     */
    @Nullable
    public abstract Session getCurrentSession();

    public abstract Throwable confirmException(Throwable e);
}
