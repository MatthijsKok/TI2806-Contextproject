package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.util.Log;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.DeviceServerService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.InstallationService;
import retrofit2.HttpException;

/**
 * A session at the Bunq bank.
 *
 * @author Jos Kuijpers
 */
public final class BunqSession extends Session {
    /*

    counter

    client-auth token
    device server id
    device IP
    session id
    session token = client auth
    token date = Response[1].Token.created
    */

    /**
     * Store the bank to access Retrofit
     */
    private BunqBank bank;

    public KeyPair clientKeyPair;
    private SignHelper signHelper;
    private PublicKey serverPublicKey;

    private String clientAuthenticationToken;

    /**
     * Simple counter for sending unique requests.
     *
     * Used for Request-Id by Bunq.
     */
    private long requestId;

    /**
     * Package-private constructor. Use BunqBank to start a session.
     */
    BunqSession(BunqBank bank) {
        this.bank = bank;

        // TODO: this is not deterministic random in the app lifetime. Is that needed though?
        SecureRandom r = new SecureRandom();
        this.requestId = r.nextLong();

        // TODO: get current ip and store
    }

    public void createKeys() {
        // Create a keypair for the client
        clientKeyPair = BunqTools.createClientKeyPair();
    }

    /**
     * Do installation of a client public key to the Bunq servers.
     */
    // TODO: return a Future instead, so objects can be chained properly?
    void doInstallation() {
        CompletableFuture<InstallationService.CreateResponse> future;
        InstallationService service;
        InstallationService.CreateRequest request;

        service = bank.getRetrofit().create(InstallationService.class);

        // Create the object to send to Bunq
        request = new InstallationService.CreateRequest(clientKeyPair.getPublic());

        future = service.createInstallation(request);

        try {
            InstallationService.CreateResponse response = future.get();

            clientAuthenticationToken = response.items.get(1).token.token;
            serverPublicKey = BunqTools.stringToPublicKey(response.items.get(2).publicKey.key);

            // Create the sign helper, now available with the server public key
            signHelper = new SignHelper(clientKeyPair, serverPublicKey);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            HttpException ex = (HttpException)e.getCause();

            try {
                Log.e("APP", ex.response().errorBody().string());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }
    }

    void doDeviceRegistration() {
        CompletableFuture<DeviceServerService.CreateResponse> future;
        DeviceServerService service;
        DeviceServerService.CreateRequest request;

        service = bank.getRetrofit().create(DeviceServerService.class);

        request = new DeviceServerService.CreateRequest();
        request.description = "Test description";
        request.secret = bank.getApiKey();

        future = service.createDevice(request);

        try {
            DeviceServerService.CreateResponse response = future.get();

            Log.d("APP", "Success!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            HttpException ex = (HttpException)e.getCause();

            try {
                Log.e("APP", ex.response().errorBody().string());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }
    }

    public SignHelper getSignHelper() {
        return signHelper;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Get a unique request id for Bunq calls.
     *
     * @return unique id (for this app session)
     */
    public String getNextRequestId() {
        return String.valueOf(++requestId);
    }

    /**
     * Get whether the session contains the public key for the server.
     *
     * Only after the installation step this is available, and only
     * after that step all calls have to have a valid signature.
     *
     * @return true when there is a server public key.
     */
    public boolean hasServerPublicKey() {
        return serverPublicKey != null;
    }

    // TODO: add saving to and loading from disk
}

/*

Create:
- create client public and private keypair (store)
- create number: 1
- send request to /installation with client pub key
  - store installationId (Response[0].id.id)
  - store token (Response.[1].Token.token) = Client-Authentication header dor [device and session]
  - store server pub key (Response[2].ServerPublicKey)

- send request to /device-server (signed)
-- with, in json, secret (API key)
-- store own IP for reference check
-- description (Random thing)
-- Client-Authentication header
  - store id of DeviceServer (is  header), (Response[0].id.id)

- send request to /session-server (signed)
- provide Client-Authentication header
- provide Client-Signature header
- JSON: secret (API key)
 - store session id (Response[0].id.id) (to end session)
 - store session token (Response[1].Token.token) = Client-Authentication

DONE


Signing

SHA256 hash, as base64 in X-Bunq-Client-Signature req, and X-Bunq-Server-Signature resp

requests:
  - for requests only: method in capitals, request endpoint url incl query string. "POST /v1/user"
  - for response only: response code
  - \n
  - headers, sorted alphabetically by key, key and value separated by ": ", only Cache-Control, User-Agent and X-Bunq-. Separate using \n
  - two \n
  - request or response body

 */
