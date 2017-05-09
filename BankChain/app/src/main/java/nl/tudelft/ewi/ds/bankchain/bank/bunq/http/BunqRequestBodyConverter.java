/*
 * Copyright (C) 2015 Square, Inc.
 * Changed by Jos Kuijpers, 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    BunqRequestBodyConverter(BunqSession session, @NonNull Gson gson, TypeAdapter<T> adapter) {
        this.session = session;
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), StandardCharsets.UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);

        adapter.write(jsonWriter, value);

        jsonWriter.close();

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        buffer.close();


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TODO: create a proper textual request body
        // TODO: sign body with key
        // TODO: set key in header

        Log.d("SESSION", "Handle converting of request body with session");

        // if there is a valid session
        //  calculate content as string
        //  sign it
        //  set header

        return requestBody;
    }
}
