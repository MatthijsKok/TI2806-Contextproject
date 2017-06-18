package nl.tudelft.ewi.ds.bankver.bank;

import android.content.Context;
import android.support.annotation.Nullable;


import java.util.Currency;
import java.util.List;

import java8.util.concurrent.CompletableFuture;


public interface Bank {

    /**
     * Create a new session.
     * <p>
     * TODO: currently not returned. Need to see how it turns out with futures etc.
     */
    CompletableFuture<Session> createSession();

    CompletableFuture<List<Transaction>> listTransactions(Account account);

    CompletableFuture<List<Party>> listUsers();

    CompletableFuture<List<Account>> listAccount(Party party);

    CompletableFuture<Boolean> sendTransaction(Transaction transaction);

    CompletableFuture<Boolean> transfer(Account fromAccount, String iban, Float amount, Currency currency, String description);

    /**
     * Get the current session of the bank.
     *
     * @return current session or null;
     */
    @Nullable
    Session getCurrentSession();

    Throwable confirmException(Throwable e);

    /**
     * Remove any (stored) session.
     */
    void deleteAnySession(@Nullable Context appContext);
}
