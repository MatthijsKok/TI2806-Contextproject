package nl.tudelft.ewi.ds.bankver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;

import nl.tudelft.ewi.ds.bankver.bank.Account;
import nl.tudelft.ewi.ds.bankver.bank.Bank;
import nl.tudelft.ewi.ds.bankver.bank.BankFactory;
import nl.tudelft.ewi.ds.bankver.bank.Party;
import nl.tudelft.ewi.ds.bankver.bank.Transaction;
import nl.tudelft.ewi.ds.bankver.cryptography.ChallengeResponse;

/**
 * Interface to BankVer.
 *
 * Make sure to call everything marked as such in a runnable.
 *
 * @author Jos Kuijpers
 */
public class BankVer {
    public enum SettingProperty {
        BANK_TYPE,

        BUNQ_API_KEY,
    }

    public enum BankType {
        BUNQ("Bunq");

        private String name;

        BankType(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final String BUNQ_URL = "http://178.62.218.153:8080/";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final Float TRANSACTION_SIZE = 0.01f;
    private static final long THROTTLE_TIME = 2000L;

    /**
     * Blockchain interface
     */
    private Blockchain blockchain;

    /**
     * Environment for bank
     */
    private Environment environment;

    private Context appContext;
    private Bank bank;

    private boolean mustThrottlePost;

    public BankVer(@NonNull Context context, @NonNull Blockchain blockchain) {
        this.appContext = context;
        this.blockchain = blockchain;
        this.environment = new Environment();

        this.mustThrottlePost = false;

        setProperty(SettingProperty.BANK_TYPE, BankType.BUNQ.toString());
    }

    /**
     * Set a setting property
     *
     * Value can't be null. To set to null, use unsetProperty instead.
     *
     * @param property the property
     * @param value the new value
     */
    public void setProperty(@NonNull SettingProperty property, @NonNull String value) {
        switch (property) {
            case BANK_TYPE: {
                switch (value) {
                    case "Bunq": {
                        environment.setBank(BankType.BUNQ.getName());
                        environment.setBankUrl(BUNQ_URL);
                        environment.setBankApiKey("");
                        destroyBank();

                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Bank type not found");
                }

                break;
            }
            case BUNQ_API_KEY: {
                if (!environment.getBank().equals("Bunq")) {
                    throw new IllegalArgumentException("Cannot set Bunq API key for non-Bunq bank");
                }

                environment.setBankApiKey(value);
                destroyBank();

                break;
            }
        }
    }

    /**
     * Set a property with a BankType value.
     *
     * @param property the property
     * @param bankType the bank type
     */
    public void setProperty(@NonNull SettingProperty property, @NonNull BankType bankType) {
        setProperty(property, bankType.toString());
    }

    /**
     * Reset a setting property
     *
     * @param property the property to reset
     */
    public void unsetProperty(@NonNull SettingProperty property) {
        switch (property) {
            case BANK_TYPE:
                environment = new Environment();
                destroyBank();
                break;

            case BUNQ_API_KEY:
                environment.setBankApiKey("");
                destroyBank();
                break;
        }
    }

    /**
     * Get a setting property
     *
     * @param property the property to set
     * @return value, or null
     */
    @Nullable
    public String getProperty(@NonNull SettingProperty property) {
        switch (property) {
            case BANK_TYPE:
                return environment.getBank();

            case BUNQ_API_KEY:
                return environment.getBankApiKey();
        }

        return null;
    }

    /**
     * Handle all online challenges, using given blockchain.
     *
     * Run inside a Runnable/Task
     */
    public void handleOnlineChallenges() {
        Log.d("IFACE", "handle online challenges");

        // Get bank + session
        Bank bank = getBank();
        if (bank == null) {
            throw new RuntimeException("Failed to create bank connection");
        }

        Account account = getBasicAccount(bank);
        if (account == null) {
            throw new RuntimeException("Failed to get hardcoded bank account");
        }

        // Read list of transactions
        List<Transaction> transactions = null;
        try {
            transactions = bank.listTransactions(account).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("IFACE", e.toString());
        }

        // Filter the challenges and responses
        for (Transaction transaction : transactions) {
            IBAN iban = new IBAN(transaction.getCounterAccount().getIban());
            PublicKey publicKey = blockchain.getPublicKeyForIBAN(iban);

            // Not in blockchain
            if (publicKey == null) {
                continue;
            }
            validatePublicKey(publicKey);

            // If is challenge
            if (ChallengeResponse.isValidChallenge(transaction.getDescription(), publicKey)) {
                PrivateKey privateKey = blockchain.getPrivateKey();
                validatePrivateKey(privateKey);

                // Create the response
                String response;
                try {
                    response = ChallengeResponse.createResponse(transaction.getDescription(), privateKey);
                } catch (SignatureException e) {
                    Log.e("IFACE", e.toString());
                    // TODO: when does this happen, what to do?
                    throw new RuntimeException("signature failed");
                } catch (InvalidKeyException e) {
                    throw new IllegalArgumentException("private key is invalid");
                }

                if (mustThrottlePost) {
                    try {
                        Thread.sleep(THROTTLE_TIME);
                        mustThrottlePost = false;
                    } catch (InterruptedException e) {
                        Log.e("IFACE", e.toString());

                        // And continue
                    }
                }

                // Send it with a cent
                try {
                    bank.transfer(account, iban.toString(), TRANSACTION_SIZE, CURRENCY, response).get();

                    mustThrottlePost = true;
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("IFACE", e.toString());

                    throw new RuntimeException(bank.confirmException(e));
                }

            } else if (ChallengeResponse.isValidResponse(transaction.getDescription(), publicKey)) {
                blockchain.setIbanVerified(publicKey, iban, transaction.getCounterAccount().getParty().getName());
            }
        }
    }

    /**
     * Handle given message. If it is a challenge, make a response. If it is a response, update blockchain.
     *
     * @param iban IBAN of the sender of the challenge
     * @param message Message of the payment
     * @return A string if a response to the sender is needed, null otherwise
     */
    @Nullable
    public String handleManualMessage(@NonNull IBAN iban, @NonNull String message) {
        PublicKey peerPublicKey = blockchain.getPublicKeyForIBAN(iban);
        validatePublicKey(peerPublicKey);

        if (ChallengeResponse.isValidChallenge(message, peerPublicKey)) {
            PrivateKey privateKey = blockchain.getPrivateKey();
            validatePrivateKey(privateKey);

            try {
                return ChallengeResponse.createResponse(message, privateKey);
            } catch (SignatureException e) {
                Log.e("IFACE", e.toString());
                // TODO: when does this happen, what to do?
                throw new RuntimeException("signature failed");
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("private key is invalid");
            }
        } else if (ChallengeResponse.isValidResponse(message, peerPublicKey)) {
            blockchain.setIbanVerified(peerPublicKey, iban, null);

            return null;
        } else {
            throw new IllegalArgumentException("message has an illegal format");
            // TODO: or silently ignore by returning null
        }
    }

    /**
     * Create a manual challenge for given account with publickey
     *
     * @param target iban of the receiver
     * @return challenge message
     */
    public String createManualChallenge(@NonNull IBAN target) throws InvalidKeyException {
        PublicKey publicKey = blockchain.getPublicKeyForIBAN(target);
        validatePublicKey(publicKey);

        PrivateKey privateKey = blockchain.getPrivateKey();
        validatePrivateKey(privateKey);

        try {
            return ChallengeResponse.createChallenge(privateKey);
        } catch (SignatureException e) {
            return null;
        }
    }

    /**
     * Create a new challenge and send it to given IBAN
     *
     * Warning: this call costs money
     *
     * Run inside a Runnable/Task
     *
     * @param target target account
     */
    public void createOnlineChallenge(@NonNull IBAN target) {
        PrivateKey privateKey = blockchain.getPrivateKey();
        validatePrivateKey(privateKey);

        String challenge;

        try {
            challenge = ChallengeResponse.createChallenge(privateKey);
        } catch (SignatureException e) {
            Log.e("IFACE", e.toString());
            // TODO: handle
            return;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("key for target is invalid");
        }

        Bank bank = getBank();
        if (bank == null) {
            throw new RuntimeException("Failed to create bank connection");
        }

        Account account = getBasicAccount(bank);
        if (account == null) {
            throw new RuntimeException("Failed to get hardcoded bank account");
        }

        try {
            bank.transfer(account, target.toString(), TRANSACTION_SIZE, CURRENCY, challenge).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("IFACE", e.toString());

            throw new RuntimeException(bank.confirmException(e));
        }
    }

    private void validatePublicKey(PublicKey publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("publicKey == null");
        }

        if (!(publicKey instanceof EdDSAPublicKey)) {
            throw new IllegalArgumentException("publicKey is not EdDSA25519");
        }
    }

    private void validatePrivateKey(PrivateKey privateKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("privateKey == null");
        }

        if (!(privateKey instanceof EdDSAPrivateKey)) {
            throw new IllegalArgumentException("privateKey is not EdDSA25519");
        }
    }

    /**
     * Create a bank with a valid session
     */
    @Nullable
    private Bank getBank() {
        if (bank == null) {
            bank = new BankFactory(environment, appContext).create();
        }

        if (bank.getCurrentSession() == null || !bank.getCurrentSession().isValid()) {
            try {
                bank.createSession().get();
            } catch (ExecutionException e) {
                Throwable t = bank.confirmException(e);

                Log.e("IFACE", t.toString());

                // TODO: handle?

                return null;
            } catch (InterruptedException e) {
                return null;
            }
        }

        return bank;
    }

    @Nullable
    private Account getBasicAccount(@NonNull Bank bank) {
        CompletableFuture<List<Party>> f1 = bank.listUsers();

        CompletableFuture<List<Account>> f2 = f1.thenCompose(parties -> {
            if (parties.isEmpty()) {
                throw new RuntimeException("No parties");
            }

            return bank.listAccount(parties.get(0));
        });

        try {
            List<Account> accounts = f2.get();
            if (accounts.isEmpty()) {
                return null;
            }

            return accounts.get(0);
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    /**
     * Remove any bank connection and session
     */
    private void destroyBank() {
        if (bank != null) {
            bank.deleteAnySession(appContext);

            bank = null;
        }
    }
}
