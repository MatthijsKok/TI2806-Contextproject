package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

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
    CompletableFuture<DeviceServerService.CreateResponse> createDevice(@Body DeviceServerService.CreateRequest request);

    /**
     * Response POJO for POST /device-server.
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
            public GenericId id;
        }
    }

    /**
     * Reques POJO
     */
    class CreateRequest {
        public String description;
        public String secret;
    }
}
