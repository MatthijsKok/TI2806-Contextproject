package nl.tudelft.ewi.ds.bankchain.bank.bunq.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqSession;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class BunqResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final BunqSession session;
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    BunqResponseBodyConverter(@NonNull BunqSession session, @NonNull Gson gson, TypeAdapter<T> adapter) {
        this.session = session;
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        // TODO: Add check for server signature
        Log.d("SESSION", "Handle converting of response body with session");

        JsonReader jsonReader = gson.newJsonReader(value.charStream());

        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
