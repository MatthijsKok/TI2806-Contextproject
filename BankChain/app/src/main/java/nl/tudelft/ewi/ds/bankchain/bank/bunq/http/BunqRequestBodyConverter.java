package nl.tudelft.ewi.ds.bankchain.bank.bunq.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqSession;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

final class BunqRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private final BunqSession session;
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    BunqRequestBodyConverter(@NonNull BunqSession session, @NonNull Gson gson, TypeAdapter<T> adapter) {
        this.session = session;
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        // TODO: create a proper textual request body
        // TODO: sign body with key
        // TODO: set key in header

        Log.d("SESSION", "Handle converting of request body with session");

        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), StandardCharsets.UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);

        adapter.write(jsonWriter, value);

        jsonWriter.close();

        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}
