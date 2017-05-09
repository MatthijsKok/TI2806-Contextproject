package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.InstallationService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.http.BunqInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class BunqBank extends Bank {
    private SessionStore sessionStore;

    /**
     * HTTP endpoint.
     */
    private Retrofit retrofit;

    public BunqBank() {
        this("https://sandbox.public.api.bunq.com/");
    }


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public BunqBank(@NonNull String url) {
        sessionStore = new SessionStore();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(Java8CallAdapterFactory.create());

        httpClient.addInterceptor(new BunqInterceptor(this));

        builder.client(httpClient.build());

        retrofit = builder.build();

    }

    @Override
    public void createSession() {
        BunqSession session = new BunqSession(this);
        sessionStore.set(session);

        // Install new client pubkey at Bunq
        session.doInstallation();


        InstallationService service = retrofit.create(InstallationService.class);

        String key = this.publicKeyToString(session.clientKeyPair.getPublic());

        Log.d("APP", "PUBLIC KEY:");
        Log.d("APP", key);
        InstallationService.CreateRequest bod = new InstallationService.CreateRequest(key);
        CompletableFuture<InstallationService.CreateResponse> future = service.createInstallation(bod);

        try {
            InstallationService.CreateResponse resp = future.get();

            Log.d("APP", "Got response from server");
            Log.d("APP", resp.Response.get(1).Token.token);
            Log.d("APP", resp.Response.get(2).ServerPublicKey.server_public_key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {

            HttpException ex = (HttpException)e.getCause();

            try {
                Log.d("APPERROR", ex.response().errorBody().string());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }

/*
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
        */
    }

    public String publicKeyToString(PublicKey pubkey) {
        return "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.encode(pubkey.getEncoded(), 0)) + "-----END PUBLIC KEY-----\n";
    }

    // TODO: string header and footer?
    public PublicKey stringToPublicKey(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.decode(string, Base64.DEFAULT);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");

        return fact.generatePublic(spec);
    }

    @Override
    public BunqSession getCurrentSession() {
        return sessionStore.get();
    }
/*
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
*/
}
