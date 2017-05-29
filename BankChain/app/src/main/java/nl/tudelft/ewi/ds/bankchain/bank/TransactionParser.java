package nl.tudelft.ewi.ds.bankchain.bank;

import android.util.Pair;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;

public abstract class TransactionParser {

    public abstract void respondToPendingChallenges(PrivateKey privateKey, Collection<Transaction> transactionCollection);

    public abstract Collection<Pair<Account, PublicKey>> getVerifiedAccountsInTransactionList(PrivateKey privateKey, Collection<Transaction> transactionCollection);
}
