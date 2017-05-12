package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.List;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTools;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Service for the installation endpoint.
 * Creates a new installation (install of public client key)
 *
 * @author Jos Kuijpers
 */
public interface InstallationService {
    @POST("v1/installation")
    CompletableFuture<CreateResponse> createInstallation(@Body CreateRequest pubkey);

    /**
     * Response POJO for POST /installation.
     */
    class CreateResponse {
        @SerializedName("Response")
        List<Item> items;

        public GenericToken getToken() {
            return items.get(1).token;
        }

        public PublicKeyItem getPublicKey() {
            return items.get(2).publicKey;
        }

        /**
         * The response can contain multiple items, list them all.
         * If not used in the element of the array, it will be null.
         */
        public class Item {
            @SerializedName("Id")
            public GenericId id;

            @SerializedName("Token")
            public GenericToken token;

            @SerializedName("ServerPublicKey")
            public PublicKeyItem publicKey;
        }

        /**
         * Item with server public key.
         */
        public class PublicKeyItem {
            @SerializedName("server_public_key")
            public String key;
        }
    }

    class CreateRequest {
        @SerializedName("client_public_key")
        public String clientPublicKey;

        // TODO add Tools static class.
        public CreateRequest(PublicKey key) {
            this(BunqTools.publicKeyToString(key));
        }

        public CreateRequest(String key) {
            clientPublicKey = key;
        }
    }
}
