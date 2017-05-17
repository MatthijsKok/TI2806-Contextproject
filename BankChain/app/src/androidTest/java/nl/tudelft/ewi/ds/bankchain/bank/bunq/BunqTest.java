package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import org.junit.Before;
import org.junit.Test;

import java8.util.concurrent.CompletableFuture;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.bank.Session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * E2E test for Bunq bank
 */
public class BunqTest {
    private Session session;
    private Throwable throwable;

    @Before
    public void before() {
        session = null;
        throwable = null;
    }

    @Test
    public void testBadUrl() throws Exception {
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.bank = "Bunq";
        env.url = "https://example.com/";
        env.apiKey = "";

        bank = new BankFactory(env).create();

        future = bank.createSession()
                .thenAccept(t -> {
                    session = t;
                })
                .exceptionally(e -> {
                    throwable = bank.confirmException(e);

                    return null;
                });

        // Run directly
        future.get();

        assertNull(session);
        assertNotNull(throwable);
        assertTrue(throwable instanceof BunqBankException);
        assertTrue(((BunqBankException) throwable).getHttpException().code() == 404);
    }

    @Test
    public void testBadApiKey() throws Exception {
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.bank = "Bunq";
        env.url = "https://sandbox.public.api.bunq.com/";
        env.apiKey = "";

        bank = new BankFactory(env).create();

        future = bank.createSession()
                .thenAccept(t -> {
                    session = t;
                })
                .exceptionally(e -> {
                    throwable = bank.confirmException(e);

                    return null;
                });

        // Run directly
        future.get();

        assertNull(session);
        assertNotNull(throwable);
        assertTrue(throwable instanceof BunqBankException);
        assertTrue(((BunqBankException) throwable).getHttpException().code() == 400);
    }

    @Test
    public void testSession() throws Exception {
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.bank = "Bunq";
        env.url = "https://sandbox.public.api.bunq.com/";
        env.apiKey = "";

        bank = new BankFactory(env).create();

        future = bank.createSession()
                .thenAccept(t -> {
                    session = t;
                })
                .exceptionally(e -> {
                    throwable = bank.confirmException(e);

                    return null;
                });

        // Run directly
        future.get();

        assertNotNull(session);
        assertNull(throwable);
    }


}
