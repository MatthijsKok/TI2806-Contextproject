package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

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
        public List<Item> Response;

        /**
         * The response can contain multiple items, list them all.
         * If not used in the element of the array, it will be null.
         */
        public class Item {
            public IdItem Id;
            public GenericToken Token;
            public PublicKeyItem ServerPublicKey;
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
            public String server_public_key;
        }
    }

    class CreateRequest {
        public String client_public_key;

        public CreateRequest(String key) {
            this.client_public_key = key;
        }
    }
}
