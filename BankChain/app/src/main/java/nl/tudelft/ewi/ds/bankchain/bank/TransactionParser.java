package nl.tudelft.ewi.ds.bankchain.bank;

import java.security.PrivateKey;
import java.util.Collection;

import nl.tudelft.ewi.ds.bankchain.cryptography.Verification;

public abstract class TransactionParser {

    public abstract void respondToPendingChallenges(Bank bank, PrivateKey privateKey, Collection<Transaction> transactionCollection);

    public abstract Collection<Verification> getVerifiedAccountsInTransactionList(Collection<Transaction> transactionCollection);
}
