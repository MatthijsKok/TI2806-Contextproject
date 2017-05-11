package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;
import android.util.Log;

import java8.util.concurrent.CompletableFuture;


import java.util.ArrayList;


import java8.util.concurrent.CompletionException;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankException;
import nl.tudelft.ewi.ds.bankchain.bank.Session;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import nl.tudelft.ewi.ds.bankchain.bank.TransactionAbstract;
//import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.ErrorResponse;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.GetTransactionsService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.InstallationService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.http.BunqInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

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
     *
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
                .thenApply((v) -> (Session)session);
    }

    /**
     * Load or create a new set of client keys
     */
    private Void loadOrCreateClientKeys() {
        // TODO: if available on disk, try
        // TODO: if failed to load from disk, make new
        session.createKeys();

        return null;
    }

    @Override
    public ArrayList<Transaction> listTransactions(Session session) {
        session = this.session;
        CompletableFuture<GetTransactionsService.CreateResponse> future;
        GetTransactionsService service;
        InstallationService.CreateRequest request;
        service = retrofit.create(GetTransactionsService.class);
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        future = service.GetTransactions();
        future.thenAccept((GetTransactionsService.CreateResponse response) ->{
            for (GetTransactionsService.CreateResponse.element trans:
                    response.items) {
                if(trans != null && trans.payment != null && trans.payment.description != null) {
                    Log.d("TRANS", trans.payment.description);
                    Transaction t = new Transaction();
                    t.Description = trans.payment.description;
                    t.Name = trans.payment.counterParty.name;
                    t.Value = trans.payment.amount.value;
                    t.IBAN = trans.payment.counterParty.iban;
                    transactions.add(t);
                }
                else{
                    Log.d("TRANS", "NULL ERROR");
                }
            }
        });

        return new ArrayList<Transaction>();
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
     *
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
            BankException be = (BankException)e.getCause();

            // Set the bank to allow the exception code to get better information
            be.setBank(this);

            return be;
        }

        return e;
    }
}
