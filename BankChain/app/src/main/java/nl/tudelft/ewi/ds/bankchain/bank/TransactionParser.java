package nl.tudelft.ewi.ds.bankchain.bank;

import java.security.PrivateKey;
import java.util.Collection;

import nl.tudelft.ewi.ds.bankchain.cryptography.Verification;

public abstract class TransactionParser {

    /**
     * Checks a list of transactions and responds to any challenges.
     * @param bank The bank to send the transactions with.
     * @param privateKey Key to sign the Responses with.
     * @param transactionCollection The transactions to check for Challenges
     */
    public abstract void respondToPendingChallenges(Bank bank, PrivateKey privateKey, Collection<Transaction> transactionCollection);

    /**
     * Gets a list of Verifications from a list of transactions.
     * @param transactionCollection The list of transactions.
     * @return A list of verified accounts with their public keys.
     */
    public abstract Collection<Verification> getVerifiedAccountsInTransactionList(Collection<Transaction> transactionCollection);
}
