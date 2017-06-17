package nl.tudelft.ewi.ds.bankver.bank.bunq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankver.bank.Session;
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.DeviceServerService;
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.InstallationService;
import nl.tudelft.ewi.ds.bankver.bank.bunq.api.SessionServerService;
import nl.tudelft.ewi.ds.bankver.bank.network.NetUtils;

/**
 * A session at the Bunq bank.
 *
 * @author Jos Kuijpers
 */
final class BunqSession extends Session {
    private static final String KEYSTORE_ALIAS = "BunqSessionKeyPair";

    /**
     * Store the bank to access Retrofit
     */
    private BunqBank bank;

    private KeyPair clientKeyPair;
    private PublicKey serverPublicKey;

    String clientAuthenticationToken;

    /**
     * Id fromt he Device Server, indicating our own device.
     */
    @SuppressWarnings("unused")
    private int deviceServerId;

    /**
     * IP address connected to the session.
     */
    private String ipAddress;

    /**
     * Session id, can be used to end the session.
     */
    @SuppressWarnings("unused")
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

    private BunqSession(BunqBank bank, DiskSession disk, KeyPair keyPair) {
        this.bank = bank;

        clientKeyPair = keyPair;
        serverPublicKey = disk.getServerPublicKey();
        clientAuthenticationToken = disk.clientAuthenticationToken;
        deviceServerId = disk.deviceServerId;
        ipAddress = disk.ipAddress;
        sessionId = disk.sessionId;
        requestId = disk.requestId;
    }

    Void createKeys() {
        clientKeyPair = BunqTools.createClientKeyPair(KEYSTORE_ALIAS);

        return null;
    }

    /**
     * Do installation of a client public key to the Bunq servers.
     *
     * The arg is required because of the limitations of futures.
     *
     * @param arg null
     * @return future
     */
    CompletableFuture<Void> doInstallation(@SuppressWarnings("unused") Void arg) {
        CompletableFuture<InstallationService.CreateResponse> future;
        InstallationService service;
        InstallationService.CreateRequest request;

        // TODO: test if this is necessary. if not, return completed

        service = bank.getRetrofit().create(InstallationService.class);

        // Create the object to send to Bunq
        request = new InstallationService.CreateRequest(clientKeyPair.getPublic());

        future = service.createInstallation(request);

        return future.thenAccept((InstallationService.CreateResponse response) -> {
            clientAuthenticationToken = response.getToken().token;
            serverPublicKey = BunqTools.stringToPublicKey(response.getPublicKey().key);
        });
    }

    /**
     * Perform the device registration step.
     *
     * The arg is required because of the limitations of futures.
     *
     * @param arg null
     * @return future
     */
    CompletableFuture<Void> doDeviceRegistration(@SuppressWarnings("unused") Void arg) {
        CompletableFuture<DeviceServerService.CreateResponse> future;
        DeviceServerService service;
        DeviceServerService.CreateRequest request;

        // TODO: test if this is necessary. if not, return completed

        service = bank.getRetrofit().create(DeviceServerService.class);

        request = new DeviceServerService.CreateRequest();
        request.description = "BankChain on Android";
        request.secret = bank.getApiKey();

        future = service.createDevice(request);

        return future.thenAccept((DeviceServerService.CreateResponse response) -> {
            ipAddress = NetUtils.getIPAddress();
            deviceServerId = response.getId().id;
        });
    }

    /**
     * Perform the session creationstep.
     *
     * The arg is required because of the limitations of futures.
     *
     * @param arg null
     * @return future
     */
    CompletableFuture<Void> doSessionStart(@SuppressWarnings("unused") Void arg) {
        CompletableFuture<SessionServerService.CreateResponse> future;
        SessionServerService service;
        SessionServerService.CreateRequest request;

        // TODO: test if this is necessary. if not, return completed

        service = bank.getRetrofit().create(SessionServerService.class);

        request = new SessionServerService.CreateRequest();
        request.secret = bank.getApiKey();

        future = service.createSession(request);

        return future.thenAccept((SessionServerService.CreateResponse response) -> {
            clientAuthenticationToken = response.getToken().token;
            sessionId = response.getId().id;
        });
    }

    /**
     * Get sign helper
     *
     * Used by the HTTP interceptor
     *
     * @return sign helper
     */
    @NonNull
    SignHelper getSignHelper() {
        return new SignHelper(clientKeyPair, serverPublicKey);
    }

    @Override
    public boolean isValid() {
        if (serverPublicKey == null || clientAuthenticationToken == null
                || clientKeyPair == null) {
            return false;
        }

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
    String getNextRequestId() {
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
    boolean hasServerPublicKey() {
        return serverPublicKey != null;
    }

    private static File getDiskFile(Context appContext) {
        return new File(appContext.getFilesDir(), "bunq.json");
    }

    @Nullable
    static BunqSession loadFromDisk(BunqBank bank, Context appContext) {
        KeyPair keyPair = BunqTools.loadClientKeyPair(KEYSTORE_ALIAS);
        if (keyPair == null) {
            return null;
        }

        DiskSession disk;

        try (Reader reader = new InputStreamReader(new FileInputStream(getDiskFile(appContext)), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().create();

            disk = gson.fromJson(reader, DiskSession.class);
        } catch (IOException e) {
            // No previously stored session found, or issues
            return null;
        }

        return new BunqSession(bank, disk, keyPair);
    }

    boolean saveToDisk(Context appContext) {
        DiskSession disk = new DiskSession(this);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(getDiskFile(appContext)), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().create();

            gson.toJson(disk, writer);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Delete the session from disk
     *
     * @return true when deletion succeeded
     */
    boolean deleteFromDisk(Context appContext) {
        if (!BunqTools.destroyClientKeyPair(KEYSTORE_ALIAS)) {
            return false;
        }

        // Set to null to invalidate the session
        clientKeyPair = null;

        return getDiskFile(appContext).delete();
    }

    private static class DiskSession {
        private String serverPublicKey;

        String clientAuthenticationToken;
        int deviceServerId;
        String ipAddress;
        int sessionId;
        long requestId;

        DiskSession(BunqSession session) {
            setServerPublicKey(session.serverPublicKey);

            clientAuthenticationToken = session.clientAuthenticationToken;
            deviceServerId = session.deviceServerId;
            ipAddress = session.ipAddress;
            sessionId = session.sessionId;
            requestId = session.requestId;
        }

        PublicKey getServerPublicKey() {
            if (serverPublicKey == null) {
                return null;
            }

            try {
                Object res = fromString(serverPublicKey);
                if (res instanceof PublicKey) {
                    return (PublicKey) res;
                } else {
                    return null;
                }
            } catch (IOException | ClassNotFoundException e) {
                return null;
            }
        }

        void setServerPublicKey(PublicKey key) {
            try {
                serverPublicKey = toString(key);
            } catch (IOException e) {
                Log.e("BUNQ", "Failed to write public key to string");
            }
        }

        // https://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
        private String toString(Serializable obj) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(obj);

            oos.close();

            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }

        private Object fromString(String s) throws IOException, ClassNotFoundException {
            byte [] data = Base64.decode(s, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream( data));

            Object o  = ois.readObject();

            ois.close();

            return o;
        }
    }
}
