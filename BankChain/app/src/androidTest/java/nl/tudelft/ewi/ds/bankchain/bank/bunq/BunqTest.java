package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import org.junit.Before;
import org.junit.Test;

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
public class BunqTest {
    private Session session;
    private Throwable throwable;

    private Bank createDummyBank() {
        Environment env;

        env = new Environment();
        env.setBank("Bunq");
        env.setBankUrl("https://sandbox.public.api.bunq.com/");
        env.setBankApiKey("");

        return new BankFactory(env).create();
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
        env.setBankUrl("https://example.com/");
        env.setBankApiKey("");

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
        env.setBank("Bunq");

        env.setBankUrl("REAL_BUNQ_URL");
        env.setBankApiKey("REAL_BUNQ_API_KEY");

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
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.setBank("Bunq");

        env.setBankUrl("REAL_BUNQ_URL");
        env.setBankApiKey("REAL_BUNQ_API_KEY");

        List<Party> Users;
        bank = new BankFactory(env).create();
        bank.createSession().get();
        Users =  bank.listUsers().get();
        Party firstUser = Users.get(0);
        assertEquals(firstUser.getId(),2002);
        assertEquals(firstUser.getName(),"Overton Onderlinge Waarborgmaatschappij");
    }


    @Test
    public void testGetAccounts() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.setBank("Bunq");

        env.setBankUrl("REAL_BUNQ_URL");
        env.setBankApiKey("REAL_BUNQ_API_KEY");

        bank = new BankFactory(env).create();
        bank.createSession().get();

        List<Account> accounts = bank.listAccount(new BunqParty("Overton Onderlinge Waarborgmaatschappij", 2002)).get();
        Account ac = accounts.get(0);
        assertEquals(ac.getParty().getId(),2002);
        assertEquals(ac.getIban(),"NL05BUNQ9900019989" +
                "");
        assertEquals(ac.getId(),2021);
    }

    @Test
    public void testListTransactions() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.setBank("Bunq");

        env.setBankUrl("REAL_BUNQ_URL");
        env.setBankApiKey("REAL_BUNQ_API_KEY");

        bank = new BankFactory(env).create();
        bank.createSession().get();

        BunqParty party = new BunqParty("Overton Onderlinge Waarborgmaatschappij",2002);
        BunqAccount account = new BunqAccount("NL05BUNQ9900019989",2021,party);

        List<Transaction> transactions = bank.listTransactions(account).get();
        Transaction t = transactions.get(transactions.size()-2);
        assertEquals(t.getId(),14191);
        assertEquals(t.getDescription(),"invoice 688");
        assertEquals(t.getAcount().getId(),2021);
        assertEquals(t.getCounterAccount().getIban(),"NL28BUNQ9900000951");
    }

    @Test
    public void testPostTransaction() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future;
        Bank bank;
        Environment env;

        env = new Environment();
        env.setBank("Bunq");

        env.setBankUrl("REAL_BUNQ_URL");
        env.setBankApiKey("REAL_BUNQ_API_KEY");

        bank = new BankFactory(env).create();
        bank.createSession().get();

        BunqParty party = new BunqParty("Overton Onderlinge Waarborgmaatschappij",2002);
        BunqAccount account = new BunqAccount("NL05BUNQ9900019989",2021,party);

        BunqParty cp = new BunqParty("Branda Monoghan", -1);
        BunqAccount ca = new BunqAccount("NL77BUNQ9900016947", -1, cp);
        BunqTransaction pay = new BunqTransaction(-0.01f, account, ca, Currency.getInstance("EUR"), "end tot end Bunqtest");

        assertTrue(bank.sendTransaction(pay).get());
    }
}
