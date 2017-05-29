package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.util.Log;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.TransactionParser;
import nl.tudelft.ewi.ds.bankchain.cryptography.Challenge;
import nl.tudelft.ewi.ds.bankchain.cryptography.Response;
import nl.tudelft.ewi.ds.bankchain.cryptography.Verification;

public class BunqTransactionParser extends TransactionParser {

    private static final int DESCRIPTION_LENGTH = 140;
    private static final int SECRET_LENGTH = 40;
    private static final int SIGNATURE_LENGTH = 128;
    private static final int PUBLIC_KEY_LENGTH = 256;

    @Override
    public void respondToPendingChallenges(PrivateKey privateKey, Collection<Transaction> transactionCollection) {
        for (Transaction transaction: transactionCollection) {
            String description = transaction.getDescription();
            if (!isValidDescriptionFormat(description)) {
                continue;
            }
            if (isValidChallenge(description)) {
                String response = Response.createResponse(description, privateKey);
                //TODO BankTransActionSender.sendTransaction with this String as description
            }
        }
    }

    @Override
    public Collection<Verification> getVerifiedAccountsInTransactionList(PrivateKey privateKey, Collection<Transaction> transactionCollection) {
        Collection<Verification> returnCollection = new ArrayList<>();
        for (Transaction transaction: transactionCollection) {
            String description = transaction.getDescription();
            if (!isValidDescriptionFormat(description)) {
                continue;
            }
            if (isValidResponse(description) || isChallenge(description)) {
                returnCollection.add(new Verification(
                        transaction.getAcount(),
                        getPublicKeyFromTransaction(transaction)));
            }
        }
        return returnCollection;
    }

    /**
     * Checks if the given string is the correct format for our system.
     * Our current format:
     * CH:ChallengeString:Signature:PublicKey => for a challenge, and
     * RE:ChallengeString:Signature:PublicKey => for a response.
     *
     * This does not check for the validity of the message or it's signature.
     *
     * @param description The String to check.
     * @return Whether the given string is in the valid format.
     */
    private boolean isValidDescriptionFormat(String description) {
        String[] array = description.split(":");
        return ((description.length() == DESCRIPTION_LENGTH) &&
                (isChallenge(description) || isResponse(description)) &&
                (array[1].length() == SECRET_LENGTH) &&
                (array[2].length() == SIGNATURE_LENGTH) &&
                (array[3].length() == PUBLIC_KEY_LENGTH));
    }

    private boolean isChallenge(String description) {
        return description.split(":")[0].equals("CH");
    }

    private boolean isValidChallenge(String description) {
        return isChallenge(description) && Response.verifyChallenge(description);
    }

    private boolean isResponse(String description) {
        return description.split(":")[0].equals("RE");
    }

    private boolean isValidResponse(String description) {
        return isResponse(description) && Challenge.verifyResponse(description);
    }

    private PublicKey getPublicKeyFromTransaction(Transaction transaction) {
        String description = transaction.getDescription();
        byte[] encodedPublicKey = Challenge.decodeMessage(description.split(":")[3]);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        try {
            return new EdDSAPublicKey(x509EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            Log.e("BunqTransActionParser", e.toString());
        }
        return null;
    }
}
