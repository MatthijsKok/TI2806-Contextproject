package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.List;

import java8.util.concurrent.CompletableFuture;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InstallationService {
    @POST("v1//installation")
    CompletableFuture<CreateResponse> createInstallation(@Body CreateRequest pubkey);

    /**
     * Response POJO for POST /installation.
     */
    class CreateResponse {
        @SerializedName("Response")
        public List<Item> items;

        /**
         * The response can contain multiple items, list them all.
         * If not used in the element of the array, it will be null.
         */
        public class Item {
            @SerializedName("Id")
            public IdItem id;

            @SerializedName("Token")
            public GenericToken token;

            @SerializedName("ServerPublicKey")
            public PublicKeyItem publicKey;
        }

        /**
         * Item with an Id.
         */
        public class IdItem {
            public int id;
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
        /*public CreateRequest(PublicKey key) {
            this(Tools.publicKeyToString(key));
        }*/

        public CreateRequest(String key) {
            this.clientPublicKey = key;
        }
    }
}
