package nl.tudelft.ewi.ds.bankver.bank.bunq;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;

import nl.tudelft.ewi.ds.bankver.bank.Bank;
import nl.tudelft.ewi.ds.bankver.bank.Transaction;
import nl.tudelft.ewi.ds.bankver.bank.TransactionParser;
import nl.tudelft.ewi.ds.bankver.cryptography.Verification;

import static nl.tudelft.ewi.ds.bankver.cryptography.ChallengeResponse.createResponse;
import static nl.tudelft.ewi.ds.bankver.cryptography.ChallengeResponse.isValidChallenge;
import static nl.tudelft.ewi.ds.bankver.cryptography.ChallengeResponse.isValidDescriptionFormat;
import static nl.tudelft.ewi.ds.bankver.cryptography.ChallengeResponse.isValidResponse;

public class BunqTransactionParser implements TransactionParser {

    @Override
    public void respondToPendingChallenges(Bank bank, PrivateKey privateKey, Collection<Transaction> transactionCollection) {
        for (Transaction transaction: transactionCollection) {
            String description = transaction.getDescription();
            if (!isValidDescriptionFormat(description)) {
                continue;
            }
            PublicKey publicKey = getPublicKeyForTransaction(transaction);
            if (isValidChallenge(description, publicKey)) {
                try {
                    String response = createResponse(description, privateKey);
                    sendTransaction(bank, transaction, response);
                } catch (SignatureException | InvalidKeyException e) {
                    Log.e("Transaction failed!", e.getLocalizedMessage());
                }
            }
        }
    }

    private void sendTransaction(Bank bank, Transaction counterTransaction, String response) {
        BunqTransaction transaction = new BunqTransaction(-0.01f, counterTransaction.getAccount(), counterTransaction.getCounterAccount(), Currency.getInstance("EUR"), response);
        bank.sendTransaction(transaction);
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
