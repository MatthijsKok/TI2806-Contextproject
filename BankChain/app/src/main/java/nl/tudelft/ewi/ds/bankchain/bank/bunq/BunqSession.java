package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.Session;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.DeviceServerService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.InstallationService;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.SessionServerService;
import nl.tudelft.ewi.ds.bankchain.bank.network.NetUtils;

/**
 * A session at the Bunq bank.
 *
 * @author Jos Kuijpers
 */
public final class BunqSession extends Session {
    /**
     * Store the bank to access Retrofit
     */
    private BunqBank bank;

    public KeyPair clientKeyPair;
    private SignHelper signHelper;
    private PublicKey serverPublicKey;

    public String clientAuthenticationToken;

    /**
     * Id fromt he Device Server, indicating our own device.
     */
    private int deviceServerId;

    /**
     * IP address connected to the session.
     */
    private String ipAddress;

    /**
     * Session id, can be used to end the session.
     */
    private int sessionId;

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
    }

    public void createKeys() {
        // Create a keypair for the client
        clientKeyPair = BunqTools.createClientKeyPair();
    }

    /**
     * Do installation of a client public key to the Bunq servers.
     */
    // TODO: return a Future instead, so objects can be chained properly?
    CompletableFuture<Void> doInstallation() {
        CompletableFuture<InstallationService.CreateResponse> future;
        InstallationService service;
        InstallationService.CreateRequest request;

        service = bank.getRetrofit().create(InstallationService.class);

        // Create the object to send to Bunq
        request = new InstallationService.CreateRequest(clientKeyPair.getPublic());

        future = service.createInstallation(request);

        return future.thenAccept((InstallationService.CreateResponse response) -> {
            clientAuthenticationToken = response.items.get(1).token.token;
            serverPublicKey = BunqTools.stringToPublicKey(response.items.get(2).publicKey.key);

            // Create the sign helper, now available with the server public key
            signHelper = new SignHelper(clientKeyPair, serverPublicKey);
        });
    }

    CompletableFuture<Void> doDeviceRegistration() {
        CompletableFuture<DeviceServerService.CreateResponse> future;
        DeviceServerService service;
        DeviceServerService.CreateRequest request;

        service = bank.getRetrofit().create(DeviceServerService.class);

        request = new DeviceServerService.CreateRequest();
        request.description = "BankChain on Android";
        request.secret = bank.getApiKey();

        future = service.createDevice(request);

        return future.thenAccept((DeviceServerService.CreateResponse response) -> {
            ipAddress = NetUtils.getIPAddress();
            deviceServerId = response.items.get(0).id.id;
        });
    }

    CompletableFuture<Void> doSessionStart() {
        CompletableFuture<SessionServerService.CreateResponse> future;
        SessionServerService service;
        SessionServerService.CreateRequest request;

        service = bank.getRetrofit().create(SessionServerService.class);

        request = new SessionServerService.CreateRequest();
        request.secret = bank.getApiKey();

        future = service.createSession(request);

        return future.thenAccept((SessionServerService.CreateResponse response) -> {
            clientAuthenticationToken = response.items.get(1).token.token;
            sessionId = response.items.get(0).id.id;
        });
    }

    public SignHelper getSignHelper() {
        return signHelper;
    }

    @Override
    public boolean isValid() {
        // A session is useless when the IP address changed as it is bound
        // to the tokens.
        if (ipAddress != null && !ipAddress.equals(NetUtils.getIPAddress())) {
            return false;
        }

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
