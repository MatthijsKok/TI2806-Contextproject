package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Collection;
import java.util.stream.Collectors;

import nl.tudelft.ewi.ds.bankchain.BankTeller;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.TransactionParser;
import nl.tudelft.ewi.ds.bankchain.blockchain.Blockchain;
import nl.tudelft.ewi.ds.bankchain.cryptography.Verification;

import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.createResponse;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidChallenge;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidDescriptionFormat;
import static nl.tudelft.ewi.ds.bankchain.cryptography.ChallengeResponse.isValidResponse;

public class BunqTransactionParser implements TransactionParser {

    @Override
    public void respondToPendingChallenges(PrivateKey privateKey, Collection<Transaction> transactions, long cutoffTime) {
        BankTeller teller = BankTeller.getBankTeller();
        Blockchain chain = teller.getBlockchain();

        transactions.stream()
                .filter(t -> t.getValue() > 0)
                .filter(t -> t.getDate().getTime() > cutoffTime)
                .filter(t -> isValidDescriptionFormat(t.getDescription()))
                .filter(t -> isValidChallenge(t.getDescription(), chain.getPublicKeyForIBAN(t.getCounterAccount().getIban())))
                .forEach(t -> {
                    try {
                        teller.sendCent(t.getCounterAccount().getIban(),
                                t.getCounterAccount().getParty().getName(),
                                createResponse(t.getDescription(), privateKey));
                    } catch (SignatureException | InvalidKeyException e) {
                        Log.e("Transaction failed!", e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public Collection<Verification> getVerifiedAccountsInTransactionList(Collection<Transaction> transactions) {
        BankTeller bankTeller = BankTeller.getBankTeller();
        Blockchain blockchain = bankTeller.getBlockchain();

        return transactions.stream()
                .filter(t -> isValidDescriptionFormat(t.getDescription()))
                .filter(t -> {
                    PublicKey key = blockchain.getPublicKeyForIBAN(t.getCounterAccount().getIban());
                    String description = t.getDescription();
                    return isValidChallenge(description, key) || isValidResponse(description, key);
                })
                .map(t -> new Verification(t.getCounterAccount(), blockchain.getPublicKeyForIBAN(t.getCounterAccount().getIban())))
                .collect(Collectors.toList());
    }
}
