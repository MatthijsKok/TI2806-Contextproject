package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import java8.util.concurrent.CompletableFuture;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Richard-HP on 17/05/2017.
 */

public interface AccountService {
    @GET("/v1/user/{userId}/monetary-account")
    CompletableFuture<ListResponse> listAccounts(@Path("userId") int user);


    class ListResponse {
        @SerializedName("Response")
        public List<Item> items;

        public class Item {
            @SerializedName("MonetaryAccountBank")
            public BunqBankAccount account;
        }

        public class BunqBankAccount {
            @SerializedName("id")
            public int id;
            @SerializedName("description")
            public String description;
            @SerializedName("alias")
            public List<Alias> aliases;
        }

        public class Alias {
            @SerializedName("type")
            public String type;
            @SerializedName("value")
            public String value;
            @SerializedName("name")
            public String name;
        }
    }
}
