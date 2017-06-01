package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.TransactionParser;
import nl.tudelft.ewi.ds.bankchain.cryptography.Verification;

import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.createResponse;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidChallenge;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidDescriptionFormat;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidResponse;

public class BunqTransactionParser extends TransactionParser {

    @Override
    public void respondToPendingChallenges(Bank bank, PrivateKey privateKey, Collection<Transaction> transactionCollection) {
        for (Transaction transaction: transactionCollection) {
            String description = transaction.getDescription();
            if (!isValidDescriptionFormat(description)) {
                continue;
            }
            PublicKey publicKey = getPublicKeyForTransaction(transaction);
            if (isValidChallenge(description, publicKey)) {
                String response = createResponse(description, privateKey);
                //TODO BankTransActionSender.sendTransaction with this String as description
            }
        }
    }

    @Override
    public Collection<Verification> getVerifiedAccountsInTransactionList(Collection<Transaction> transactions) {
        Collection<Verification> verifications = new ArrayList<>();
        for (Transaction transaction: transactions) {
            String description = transaction.getDescription();
            if (!isValidDescriptionFormat(description)) {
                continue;
            }
            PublicKey publicKey = getPublicKeyForTransaction(transaction);
            if (isValidResponse(description, publicKey) || isValidChallenge(description, publicKey)) {
                verifications.add(new Verification(
                        transaction.getCounterAccount(),
                        publicKey));
            }
        }
        return verifications;
    }

    private PublicKey getPublicKeyForTransaction(Transaction transaction) {
        //TODO implement method
        return null;
    }
}
