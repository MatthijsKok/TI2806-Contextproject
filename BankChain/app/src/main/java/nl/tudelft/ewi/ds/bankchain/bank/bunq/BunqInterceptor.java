package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * HTTP interceptor for adding Bunq specific requirements.
 *
 * Adds headers and signature to responses.
 * Verifies signature in requests.
 *
 * @author Jos Kuijpers
 */
class BunqInterceptor implements Interceptor {
    private final static String USER_AGENT = "BankChain";

    private final static String SERVER_SIGNATURE = "X-Bunq-Server-Signature";
    private final static String CLIENT_SIGNATURE = "X-Bunq-Client-Signature";

    private final static String CLIENT_REQUEST_ID = "X-Bunq-Client-Request-Id";
    private final static String CLIENT_RESPONSE_ID = "X-Bunq-Client-Response-Id";

    private final static String REGION = "X-Bunq-Region";
    private final static String LANGUAGE = "X-Bunq-Language";
    private final static String GEOLOCATION = "X-Bunq-Geolocation";

    private final static String CLIENT_AUTHENTICATION = "X-Bunq-Client-Authentication";

    private BunqBank bank;

    BunqInterceptor(BunqBank bank) {
        this.bank = bank;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = createRequest(chain.request());
        Response response = chain.proceed(request);

        return createResponse(response);
    }

    private Request createRequest(Request incoming) throws IOException {
        Request.Builder builder = incoming.newBuilder();
        BunqSession session = bank.getCurrentSession();

        // Add required default headers
        builder.addHeader(CLIENT_REQUEST_ID, bank.getCurrentSession().getNextRequestId());
        builder.addHeader(REGION, "en_US");
        builder.addHeader(LANGUAGE, "en_US");
        builder.addHeader(GEOLOCATION, "0 0 0 0 000");
        builder.addHeader("Cache-Control", "nocache");

        builder.removeHeader("User-Agent");
        builder.addHeader("User-Agent", USER_AGENT);

        if (session.clientAuthenticationToken != null) {
            builder.addHeader(CLIENT_AUTHENTICATION, session.clientAuthenticationToken);
        }

        if (session.hasServerPublicKey()) {
            // Rebuild request to add the new headers
            Request incomingWithHeaders = builder.build();
            builder = incomingWithHeaders.newBuilder();

            // Create a string that is in the proper format, as defined by Bunq
            String signable = createSignable(incomingWithHeaders);
            String signature = bank.getCurrentSession().getSignHelper().sign(signable);

            builder.addHeader(CLIENT_SIGNATURE, signature);
        }

        return builder.build();
    }

    private Response createResponse(Response incoming) throws IOException {
        BunqSession session = bank.getCurrentSession();
        Response.Builder builder = incoming.newBuilder();

        if (session != null && session.hasServerPublicKey()) {
            // Without a header this response is spoofed
            String serverSignature = incoming.header(SERVER_SIGNATURE);
            if (serverSignature == null) {
                // TODO: make proper exceptions that subclass IOException
                throw new IOException("Bunq was spoofed: no server signature found");
            }

            String signable = createSignable(incoming, builder);
            if (!bank.getCurrentSession().getSignHelper().verify(signable, serverSignature)) {
                throw new IOException("Bunq was spoofed: server signature is wrong");
            }
        }

        return builder.build();
    }

    @NonNull
    private String createSignable(Request request) throws IOException {
        okio.Buffer buffer = new Buffer();

        request.body().writeTo(buffer);
        String body = buffer.readByteString().string(StandardCharsets.UTF_8);
        String prefix = request.method() + " " + request.url().encodedPath();

        return createSignable(prefix, request.headers(), body, false);
    }

    @NonNull
    private String createSignable(Response response, Response.Builder builder) throws IOException {
        // Because both this interceptor and the code down the chain reads the body,
        // we need to make a new one: the body can only be read once.
        // http://stackoverflow.com/questions/38641565/okhttp-throwing-an-illegal-state-exception-when-i-try-to-log-the-network-respons
        // https://github.com/square/okhttp/issues/1240
        ResponseBody body = response.body();
        String bodyString = body.string();
        MediaType contentType = body.contentType();

        builder.body(ResponseBody.create(contentType, bodyString));

        return createSignable(String.valueOf(response.code()), response.headers(), bodyString, true);
    }

    /**
     * Create a signable message from input.
     *
     * @url https://doc.bunq.com/api/1/page/signing
     */
    @NonNull
    private String createSignable(String prefix, Headers headers, String body, boolean bunqHeadersOnly) {
        StringBuilder sb = new StringBuilder();

        // Add prefix
        sb.append(prefix);
        sb.append('\n');

        /*
        Sorted order of supported headers.
        Do not send the signature headers here, that is silly because we are
        calculating them.

        Cache-Control
        User-Agent
        X-Bunq-Client-Authentication
        X-Bunq-Client-Request-Id
        X-Bunq-Client-Response-Id
        X-Bunq-Client-Signature
        X-Bunq-Geolocation
        X-Bunq-Language
        X-Bunq-Region
        X-Bunq-Server-Signature
        */

        // Add all required headers, alphabetically
        if (!bunqHeadersOnly) {
            buildHeader(headers, "Cache-Control", sb);
            buildHeader(headers, "User-Agent", sb);
        }
        buildHeader(headers, CLIENT_AUTHENTICATION, sb);
        buildHeader(headers, CLIENT_REQUEST_ID, sb);
        buildHeader(headers, CLIENT_RESPONSE_ID, sb);
        buildHeader(headers, GEOLOCATION, sb);
        buildHeader(headers, LANGUAGE, sb);
        buildHeader(headers, REGION, sb);

        // Bunq: Two '\n' (linefeed) newlines (even when there is no body).
        // The first one is already done at the end of the last header
        sb.append('\n');

        // Add content
        sb.append(body);

        return sb.toString();
    }

    /**
     * Write a header to a string builder, if the header exists.
     *
     * Using the Bunq required format.
     */
    private void buildHeader(Headers headers, String header, StringBuilder sb) {
        String value = headers.get(header);

        if (value != null) {
            sb.append(header);
            sb.append(": ");
            sb.append(value);
            sb.append('\n');
        }
    }
}
