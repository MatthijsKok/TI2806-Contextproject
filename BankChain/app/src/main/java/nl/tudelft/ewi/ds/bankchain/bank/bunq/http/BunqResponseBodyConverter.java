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
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqSession;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class BunqResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final BunqSession session;
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    BunqResponseBodyConverter(BunqSession session, @NonNull Gson gson, TypeAdapter<T> adapter) {
        this.session = session;
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());

        // If a session exists and it has a public key
        // Get the server sign
        // if server sign is not available, exception (otherwise removing the header circumvents checks)
        // Calculate body
        // Verify body with signature

        // TODO: Add check for server signature
        Log.d("SESSION", "Handle converting of response body with session");

        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
