package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.List;

import java8.util.concurrent.CompletableFuture;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqTools;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Service for session-server endpoint.
 * Create a new session
 *
 * @author Jos Kuijpers
 */
public interface SessionServerService {
    @POST("v1/session-server")
    CompletableFuture<CreateResponse> createSession(@Body CreateRequest secret);

    /**
     * Response POJO for POST /session-server
     */
    class CreateResponse {
        @SerializedName("Response")
        public List<InstallationService.CreateResponse.Item> items;

        /**
         * The response can contain multiple items, list them all.
         * If not used in the element of the array, it will be null.
         */
        public class Item {
            @SerializedName("Id")
            public GenericId id;

            @SerializedName("Token")
            public GenericToken token;

//            @SerializedName("UserCompany")
            // TODO: look into data that might be useful here
        }
    }

    class CreateRequest {
        /**
         * API key
         */
        public String secret;
    }
}
