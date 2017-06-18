package nl.tudelft.ewi.ds.bankver.bank.bunq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java8.util.concurrent.CompletableFuture;


import java.util.ArrayList;
import java.util.Currency;
import java.util.List;


import java8.util.concurrent.CompletionException;

import nl.tudelft.ewi.ds.bankver.bank.Account;
import nl.tudelft.ewi.ds.bankver.bank.Bank;
import nl.tudelft.ewi.ds.bankver.bank.BankException;
import nl.tudelft.ewi.ds.bankver.bank.Session;
import nl.tudelft.ewi.ds.bankver.bank.Transaction;

import nl.tudelft.ewi.ds.bankver.bank.Party;
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.AccountService;
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.PaymentService;


import nl.tudelft.ewi.ds.bankver.bank.bunq.api.UserService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import nl.tudelft.ewi.ds.bankver.bank.bunq.retrofit.Java8CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Bank implementation for the Bunq bank.
 *
 * @author Jos Kuijpers
 */
public final class BunqBank implements Bank {
    /**
     * Current session
     */
    private BunqSession session;

    /**
     * HTTP endpoint.
     */
    private Retrofit retrofit;

    /**
     * API key from Bunq
     */
    private String apiKey;

    /**
     * App context for loading and writing files
     */
    private Context appContext;

    /**
     * Create a new bank inferface with given API url.
     * <p>
     * Creates a proper HTTP client and a session store.
     *
     * @param url URL of the Bunq API
     */
    public BunqBank(@NonNull Context appContext, @NonNull String url, @NonNull String apiKey) {
        this.apiKey = apiKey;
        this.appContext = appContext;

        // Create a retrofit system with a JSON parser and Java8 async system
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(Java8CallAdapterFactory.create());

        // Use the underlying OkHTTP to add interceptors
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .addInterceptor(new BunqInterceptor(this))
                .followRedirects(false)
                .followSslRedirects(false);

        // Create the client
        builder.client(httpClient.build());
        retrofit = builder.build();
    }

    @Override
    public CompletableFuture<Session> createSession() {
        // Attempt to load a session
        session = BunqSession.loadFromDisk(this, appContext);
        if (session != null && session.isValid()) {
            return CompletableFuture.completedFuture(session);
        }

        // Create a new session instead
        session = new BunqSession(this);

        // Install new client pubkey at Bunq (using some functional programming)
        return CompletableFuture
                .supplyAsync(session::createKeys)
                .thenComposeAsync(session::doInstallation)
                .thenComposeAsync(session::doDeviceRegistration)
                .thenComposeAsync(session::doSessionStart)

                // Set the session as a value (use an upcast)
                .thenApply((v) -> {
                    session.saveToDisk(appContext);

                    return (Session) session;
                });
    }

    @Override
    public CompletableFuture<List<Transaction>> listTransactions(Account account) {
        CompletableFuture<PaymentService.ListResponse> future;
        PaymentService service;

        service = retrofit.create(PaymentService.class);

        future = service.listPayments(account.getParty().getId(), account.getId());

        return future.thenApply((PaymentService.ListResponse response) -> {
            List<Transaction> transactions = new ArrayList<Transaction>();

            for (PaymentService.ListResponse.Item item : response.items) {
                transactions.add(new BunqTransaction(item.payment, account));
            }

            return transactions;
        });
    }

    /**
     * returns a list of users that are linked to this account
     *
     * @return List of Parties
     */
    @Override
    public CompletableFuture<List<Party>> listUsers() {
        CompletableFuture<UserService.ListResponse> future;
        UserService service;

        service = retrofit.create(UserService.class);
        future = service.getUsers();
        return future.thenApply((UserService.ListResponse response) -> {
            List<Party> parties = new ArrayList<Party>();

            for (UserService.ListResponse.Item item : response.items) {
                parties.add(new BunqParty(item.user));
            }

            return parties;
        });
    }

    @Override
    public CompletableFuture<List<Account>> listAccount(Party party) {
        CompletableFuture<AccountService.ListResponse> future;
        AccountService service;

        service = retrofit.create(AccountService.class);
        future = service.listAccounts(party.getId());
        return future.thenApply((AccountService.ListResponse response) -> {
            List<Account> accounts = new ArrayList<Account>();

            for (AccountService.ListResponse.Item item : response.items) {
                accounts.add(new BunqAccount(item.account, party));
            }

            return accounts;
        });
    }

    @Override
    public CompletableFuture<Boolean> sendTransaction(Transaction transaction) {
        BunqTransaction tran = (BunqTransaction) transaction;
        CompletableFuture<PaymentService.PostResponse> future;
        PaymentService service;

        service = retrofit.create(PaymentService.class);

        future = service.createPayment(tran.convertToRequest(), tran.getAccount().getParty().getId(), tran.getAccount().getId());
        return future.thenApply((PaymentService.PostResponse response) -> {
            int id = response.items.get(0).id.id;
            if (id > 0) {
                tran.setId(id);
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> transfer(Account fromAccount, String iban, Float amount, Currency currency, String description) {
        // Use a dummy name as it is unverified
        BunqParty cp = new BunqParty("Unverified", -1);
        BunqAccount toAccount = new BunqAccount(iban, -1, cp);

        BunqTransaction transaction = new BunqTransaction(-1 * amount, fromAccount, toAccount, currency, description);

        return sendTransaction(transaction);
    }

    @Override
    public BunqSession getCurrentSession() {
        return session;
    }

    /**
     * Get the Retrofit HTTP client.
     *
     * @return http client.
     */
    @NonNull
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * Get the API key.
     * <p>
     * Only accessable by the Bunq package.
     *
     * @return Api key
     */
    @NonNull
    String getApiKey() {
        return apiKey;
    }

    @Override
    public Throwable confirmException(Throwable e) {
        if (e == null) {
            return null;
        }

        if (e instanceof CompletionException && e.getCause() instanceof BankException) {
            BankException be = (BankException) e.getCause();

            // Set the bank to allow the exception code to get better information
            be.setBank(this);

            return be;
        }

        return e;
    }

    @Override
    public void deleteAnySession(@Nullable Context appContext) {
        if (appContext != null) {
            BunqSession.deleteAnyFromDisk(appContext);
        }

        session = null;
    }
}
