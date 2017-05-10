package nl.tudelft.ewi.ds.bankchain.bank;

import android.support.annotation.Nullable;

import java.util.List;

public abstract class Bank {

    // public abstract boolean isSandbox();

    /**
     * Create a new session.
     *
     * TODO: currently not returned. Need to see how it turns out with futures etc.
     */
    public abstract void createSession();
//
//    public abstract Optional<Session> createSession();
//
     public abstract List<Transaction> listTransactions(Session session);
//
//    public abstract boolean sendTransaction(Session, Transaction);

    /**
     * Get the current session of the bank.
     *
     * TODO: it would, at the moment, be possible to switch sessions. For that it might be better to expose SessionStore.
     *
     * @return current session or null;
     */
    @Nullable
    public abstract Session getCurrentSession();
}
