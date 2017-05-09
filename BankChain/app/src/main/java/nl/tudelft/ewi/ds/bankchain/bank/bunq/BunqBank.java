package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.http.BunqConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.http.GET;

public final class BunqBank extends Bank {
    private SessionStore sessionStore;

    /**
     * HTTP endpoint.
     */
    private Retrofit retrofit;

    public BunqBank() {
        this("https://jsonplaceholder.typicode.com/");
    }

    public BunqBank(@NonNull String url) {
        sessionStore = new SessionStore();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(BunqConverterFactory.create(sessionStore))
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .build();
    }

    @Override
    public void createSession() {
        BunqSession session = new BunqSession(this);
        sessionStore.set(session);



        // Install new client pubkey at Bunq
        session.doInstallation();





        // TODO: remove this, is a test
        Service service = retrofit.create(Service.class);

        CompletableFuture<List<Post>> future = service.body();
        try {
            List<Post> posts = future.get();
            Log.d("APP", String.format("%d", posts.size()));
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Session getCurrentSession() {
        return sessionStore.get();
    }

    // TODO: Remove this
    public class Post {
        int userId;
        int id;
        String title;
        String body;
    }

    // TODO: Remove this
    interface Service {
        @GET("/posts") CompletableFuture<List<Post>> body();
    }

}
