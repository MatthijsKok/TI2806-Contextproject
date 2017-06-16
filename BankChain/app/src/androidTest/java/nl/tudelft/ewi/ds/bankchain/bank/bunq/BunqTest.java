package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import java8.util.concurrent.CompletionException;

import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankFactory;
import nl.tudelft.ewi.ds.bankchain.Environment;
import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * E2E test for Bunq bank
 */
@RunWith(AndroidJUnit4.class)
public class BunqTest {
    private Session session;
    private Throwable throwable;

    private InputStream getInputStream(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    private Context instrumentationCtx;

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @NonNull
    private Bank getRealBank() {
        InputStream in = getInputStream("res/raw/environment.xml");
        Environment env;

        try {
            env = Environment.loadDefaults(in);

            Log.d("BUNQTEST", env.getBankUrl());
        } catch (IOException e) {
            throw new AssertionError("Environment could not be loaded");
        }

        return new BankFactory(env, instrumentationCtx).create();
    }

    private Bank createDummyBank() {
        Environment env;

        env = new Environment();
        env.setBank("Bunq");
        env.setBankUrl("https://sandbox.public.api.bunq.com/");
        env.setBankApiKey("");

        return new BankFactory(env, instrumentationCtx).create();
    }

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
        env.setBank("Bunq");
        env.setBankUrl("https://doesnotextexample.com/");
        env.setBankApiKey("");

        bank = new BankFactory(env, instrumentationCtx).create();

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
        assertTrue(throwable instanceof CompletionException);
        assertTrue(throwable.getCause() instanceof UnknownHostException);
    }

    @Test
    public void testBadApiKey() throws Exception {
        CompletableFuture<Void> future;
        Bank bank;

        bank = createDummyBank();

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

        Log.e("BUNQ", throwable.toString());

        assertNull(session);
        assertNotNull(throwable);
        assertTrue(throwable instanceof BunqBankException);
        assertTrue(((BunqBankException) throwable).getHttpException().code() == 400);
    }

    @Test
    public void testSession() throws Exception {
        CompletableFuture<Void> future;
        Bank bank;

        bank = getRealBank();

        future = bank.createSession()
                .thenAccept(t -> {
                    session = t;
                })
                .exceptionally(e -> {
                    throwable = bank.confirmException(e);
                    if (e instanceof CompletionException) {
                        e.getCause().printStackTrace();
                    }
                    return null;
                });

        // Run directly
        future.get();

        assertNotNull(session);
        assertNull(throwable);
    }

    // When running confirmation through null it should not crash but return null
    @Test
    public void testExceptionConfirmationNull() {
        Bank bank = createDummyBank();

        assertNull(bank.confirmException(null));
    }

    // When running confirmation through an unsupported exception, the exception must not be altered
    @Test
    public void testExceptionConfirmationNotFuture() {
        Bank bank = createDummyBank();

        Throwable thr = new IllegalArgumentException("test");

        assertTrue(bank.confirmException(thr) == thr);
    }

    @Test
    public void testGetUsers() throws ExecutionException, InterruptedException {
        Bank bank = getRealBank();
        List<Party> users;

        bank.createSession().get();
        users = bank.listUsers().get();
        assertEquals(users.size(), 1);
        Party firstUser = users.get(0);
        assertEquals(firstUser.getId(), 2002);
        assertEquals(firstUser.getName(), "Overton Onderlinge Waarborgmaatschappij");
    }


    @Test
    public void testGetAccounts() throws ExecutionException, InterruptedException {
        Bank bank = getRealBank();

        bank.createSession().get();

        List<Account> accounts = bank.listAccount(new BunqParty("Overton Onderlinge Waarborgmaatschappij", 2002)).get();
        assertEquals(accounts.size(), 1);
        Account ac = accounts.get(0);
        assertEquals(ac.getParty().getId(), 2002);
        assertEquals(ac.getIban(), "NL05BUNQ9900019989" +
                "");
        assertEquals(ac.getId(), 2021);
    }


    @Test
    public void testPostTransaction() throws ExecutionException, InterruptedException {
        Bank bank = getRealBank();

        bank.createSession().get();

        BunqParty party = new BunqParty("Overton Onderlinge Waarborgmaatschappij", 2002);
        BunqAccount account = new BunqAccount("NL05BUNQ9900019989", 2021, party);

        BunqParty cp = new BunqParty("Branda Monoghan", -1);
        BunqAccount ca = new BunqAccount("NL77BUNQ9900016947", -1, cp);
        BunqTransaction pay = new BunqTransaction(-0.01f, account, ca, Currency.getInstance("EUR"), "end tot end Bunqtest");

        assertTrue(bank.sendTransaction(pay).get());
    }

    @Test
    public void testListTransactions() throws ExecutionException, InterruptedException {
        Bank bank = getRealBank();
        bank.createSession().get();

        BunqParty party = new BunqParty("Overton Onderlinge Waarborgmaatschappij", 2002);
        BunqAccount account = new BunqAccount("NL05BUNQ9900019989", 2021, party);

        List<Transaction> transactions = bank.listTransactions(account).get();
        assertTrue(transactions.size() > 5);
        Transaction t = transactions.get(transactions.size() - 2);
        assertEquals(t.getDescription(), "end tot end Bunqtest");
        assertEquals(t.getAccount().getId(), 2021);
        assertEquals(t.getCounterAccount().getIban(), "NL77BUNQ9900016947");
    }
}
