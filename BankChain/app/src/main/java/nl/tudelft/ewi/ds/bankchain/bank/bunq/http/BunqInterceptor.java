package nl.tudelft.ewi.ds.bankchain.bank.bunq.http;

import android.util.Log;

import java.io.IOException;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqBank;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqSession;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BunqInterceptor implements Interceptor {
    private final static String USER_AGENT = "BankChain";

    private final static String SERVER_SIGNATURE = "X-Bunq-Server-Signature";
    private final static String CLIENT_SIGNATURE = "X-Bunq-Client-Signature";
    private final static String CLIENT_REQUEST_ID = "X-Bunq-Client-Request-Id";
    private final static String REGION = "X-Bunq-Region";
    private final static String LANGUAGE = "X-Bunq-Language";
    private final static String GEOLOCATION = "X-Bunq-Geolocation";

    private BunqBank bank;

    public BunqInterceptor(BunqBank bank) {
        this.bank = bank;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = this.createRequest(chain.request());
        Response response = chain.proceed(request);

        return this.createResponse(response);
    }

    private Request createRequest(Request incoming) throws IOException {
        Request.Builder builder = incoming.newBuilder();

        Log.d("INTERCEPTOR", "Intercepted!");

        // Add required default headers
        builder.addHeader(CLIENT_REQUEST_ID, bank.getCurrentSession().getNextRequestId());
        builder.addHeader(REGION, "en_US");
        builder.addHeader(LANGUAGE, "en_US");
        builder.addHeader(GEOLOCATION, "0 0 0 0 000");
        builder.addHeader("Cache-Control", "nocache");

        builder.removeHeader("User-Agent");
        builder.addHeader("User-Agent", USER_AGENT);


        // TODO: create a proper textual request body
        // TODO: sign body with key
        // TODO: set key in header

        Log.d("SESSION", "Handle converting of request body with session");

        // if there is a valid session
        //  calculate content as string
        //  sign it
        //  set header
        if (bank.getCurrentSession().hasServerPublicKey()) {
            Log.d("APP", "Session is valid, create signature");
        }

        return builder.build();
    }

    private Response createResponse(Response incoming) throws IOException {
        Response.Builder builder = incoming.newBuilder();
        BunqSession session = bank.getCurrentSession();

        // If a session exists and it has a public key
        // Get the server sign
        // if server sign is not available, exception (otherwise removing the header circumvents checks)
        // Calculate body
        // Verify body with signature

        // TODO: Add check for server signature
        Log.d("SESSION", "Handle converting of response body with session");

        if (session != null && session.hasServerPublicKey()) {

            // Without a header this response is spoofed
            String serverSignature = incoming.header(SERVER_SIGNATURE);
            if (serverSignature == null) {
                // TODO: make proper exceptions that subclass IOException
                throw new IOException("Bunq was spoofed: no server signature found");
            }
        }

        return builder.build();
    }
}
