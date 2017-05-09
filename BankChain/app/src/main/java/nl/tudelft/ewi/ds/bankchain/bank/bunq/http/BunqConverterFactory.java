package nl.tudelft.ewi.ds.bankchain.bank.bunq.http;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.SessionStore;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class BunqConverterFactory extends Converter.Factory {
    /**
     * Stores a reference to zero or more session.
     */
    private SessionStore sessionStore;

    private Gson gson;

    /**
     * Create a new factory.
     *
     * @param sessionStore the session store
     * @return factory
     */
    public static BunqConverterFactory create(@NonNull SessionStore sessionStore) {
        return new BunqConverterFactory(sessionStore, new Gson());
    }

    /**
     * Create a new factory with given Gson.
     *
     * @param sessionStore the session store
     * @param gson the gson
     * @return factory
     */
    public static BunqConverterFactory create(@NonNull SessionStore sessionStore, Gson gson) {
        return new BunqConverterFactory(sessionStore, gson);
    }

    /**
     * Private constructor to force using create()
     */
    private BunqConverterFactory(SessionStore sessionStore, Gson gson) {
        this.sessionStore = sessionStore;
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new BunqResponseBodyConverter<>(sessionStore.get(), gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));

        return new BunqRequestBodyConverter<>(sessionStore.get(), gson, adapter);
    }
}
