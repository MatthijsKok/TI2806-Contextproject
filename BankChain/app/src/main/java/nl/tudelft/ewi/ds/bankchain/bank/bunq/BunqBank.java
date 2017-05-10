package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.http.BunqInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Bank implementation for the Bunq bank.
 *
 * @author Jos Kuijpers
 */
public final class BunqBank extends Bank {
    private SessionStore sessionStore;

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
        sessionStore = new SessionStore();
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
    public void createSession() {
        BunqSession session = new BunqSession(this);
        sessionStore.set(session);

        // TODO: the keys could be stored instead
        session.createKeys();

        // Install new client pubkey at Bunq
        session.doInstallation();

        session.doDeviceRegistration();

        session.doSessionStart();
    }

    @Override
    public BunqSession getCurrentSession() {
        return sessionStore.get();
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
}
