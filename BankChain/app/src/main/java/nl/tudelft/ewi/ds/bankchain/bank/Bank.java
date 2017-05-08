package nl.tudelft.ewi.ds.bankchain.bank;

public abstract class Bank {

    // public abstract boolean isSandbox();

    public abstract Session createSession();
//
//    public abstract Optional<Session> createSession();
//
//    public abstract Optional<List<Transaction>> listTransactions(Session);
//
//    public abstract boolean sendTransaction(Session, Transaction);
}
