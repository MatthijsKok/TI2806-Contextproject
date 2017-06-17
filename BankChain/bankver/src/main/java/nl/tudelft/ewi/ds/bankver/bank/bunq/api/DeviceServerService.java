package nl.tudelft.ewi.ds.bankver.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import java8.util.concurrent.CompletableFuture;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Service for the Device Server endpoint
 *
 * @author Jos Kuijpers
 */
public interface DeviceServerService {
    @POST("v1/device-server")
    CompletableFuture<CreateResponse> createDevice(@Body CreateRequest request);

    /**
     * Response POJO for POST /device-server.
     */
    class CreateResponse {
        @SerializedName("Response")
        List<Item> items;

        public GenericId getId() {
            return items.get(0).id;
        }

        /**
         * The response can contain multiple items, list them all.
         * If not used in the element of the array, it will be null.
         */
        public class Item {
            @SerializedName("Id")
            public GenericId id;
        }
    }

    /**
     * Request POJO
     */
    class CreateRequest {
        /**
         * Description of this device, for device listing
         */
        public String description;

        /**
         * API key
         */
        public String secret;
    }
}
