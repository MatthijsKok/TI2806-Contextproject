package nl.tudelft.ewi.ds.bankchain.bank;

public abstract class Bank {

    // public abstract boolean isSandbox();

    public abstract void createSession();
//
//    public abstract Optional<Session> createSession();
//
//    public abstract Optional<List<Transaction>> listTransactions(Session);
//
//    public abstract boolean sendTransaction(Session, Transaction);

    public abstract Session getCurrentSession();
}
