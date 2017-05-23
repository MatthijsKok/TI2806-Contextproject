package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;

import java8.util.concurrent.CompletableFuture;


import java.util.ArrayList;
import java.util.List;


import java8.util.concurrent.CompletionException;

import nl.tudelft.ewi.ds.bankchain.bank.Account;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankException;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.Transaction;

import nl.tudelft.ewi.ds.bankchain.bank.Party;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.AccountService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.PaymentService;


import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.UserService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.retrofit.Java8CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Bank implementation for the Bunq bank.
 *
 * @author Jos Kuijpers
 */
public final class BunqBank extends Bank {
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
     * Create a new bank inferface with given API url.
     * <p>
     * Creates a proper HTTP client and a session store.
     *
     * @param url URL of the Bunq API
     */
    public BunqBank(@NonNull String url, @NonNull String apiKey) {
        this.apiKey = apiKey;

        // Create a retrofit system with a JSON parser and Java8 async system
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(Java8CallAdapterFactory.create());

        // Use the underlying OkHTTP to add interceptors
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new BunqInterceptor(this));

        // Create the client
        builder.client(httpClient.build());
        retrofit = builder.build();
    }

    @Override
    public CompletableFuture<Session> createSession() {
        session = new BunqSession(this);

        // Install new client pubkey at Bunq (using some functional programming)
        return CompletableFuture
                .supplyAsync(this::loadOrCreateClientKeys)
                .thenComposeAsync(session::doInstallation)
                .thenComposeAsync(session::doDeviceRegistration)
                .thenComposeAsync(session::doSessionStart)

                // Set the session as a value (use an upcast)
                .thenApply((v) -> (Session) session);
    }

    /**
     * Load or create a new set of client keys
     */
    private Void loadOrCreateClientKeys() {
        // Attempt to load
        if (!session.load()) {
            session.createKeys();
        }

        return null;
    }

    @Override
    public CompletableFuture<List<? extends Transaction>> listTransactions(Account account) {
        CompletableFuture<PaymentService.ListResponse> future;
        PaymentService service;

        service = retrofit.create(PaymentService.class);

        // TODO: get values somewhere else
        future = service.listPayments(account.getParty().getId(), account.getId());

        return future.thenApply((PaymentService.ListResponse response) -> {
            List<BunqTransaction> transactions = new ArrayList<BunqTransaction>();

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
    public CompletableFuture<List<? extends Party>> listUsers() {
        CompletableFuture<UserService.ListResponse> future;
        UserService service;

        service = retrofit.create(UserService.class);
        future = service.getUsers();
        return future.thenApply((UserService.ListResponse response) -> {
            List<BunqParty> parties = new ArrayList<BunqParty>();

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
}
