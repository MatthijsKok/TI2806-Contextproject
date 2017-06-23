package nl.tudelft.ewi.ds.bankchain.bank;

import java.security.PrivateKey;
import java.util.Collection;

import nl.tudelft.ewi.ds.bankchain.cryptography.Verification;

public interface TransactionParser {

    /**
     * Checks a list of transactions and responds to any challenges.
     * @param privateKey Key to sign the Responses with.
     * @param transactions The transactions to check for Challenges
     * @param cutoffTime The time before which you don't take in account transactions.
     */
    public void respondToPendingChallenges(PrivateKey privateKey, Collection<Transaction> transactions, long cutoffTime);

    /**
     * Gets a list of Verifications from a list of transactions.
     * @param transactionCollection The list of transactions.
     * @return A list of verified accounts with their public keys.
     */
    public Collection<Verification> getVerifiedAccountsInTransactionList(Collection<Transaction> transactionCollection);
}
