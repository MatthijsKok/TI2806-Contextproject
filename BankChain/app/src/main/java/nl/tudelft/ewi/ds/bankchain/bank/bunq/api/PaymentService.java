package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.Currency;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import java8.util.concurrent.CompletableFuture;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * API for the payment endpoint.
 *
 * @author Richard
 * @note This is MVP
 */
public interface PaymentService {
    @GET("/v1/user/{user}/monetary-account/{account}/payment")
    CompletableFuture<ListResponse> listPayments(@Path("user") int user,
                                                 @Path("account") int account);

    @POST("/v1/user/{user}/monetary-account/{account}/payment")
    CompletableFuture<PostResponse> createPayment(@Body PostRequest request,
                                                  @Path("user") int user,
                                                  @Path("account") int account);

    class ListResponse {
        @SerializedName("Response")
        public List<Item> items;

        public class Item {
            @SerializedName("Payment")
            public Payment payment;
        }

        public class Payment {
            public String created;
            public String description;
            public Amount amount;
            public int id;

            @SerializedName("counterparty_alias")
            public CounterParty counterParty;

            public Date getCreationDate() {
                return Date.valueOf(created);
            }
        }


        public class CounterParty {
            @SerializedName("iban")
            public String iban;

            @SerializedName("display_name")
            public String name;
        }

        public class Amount {
            @SerializedName("value")
            private String value;

            @SerializedName("currency")
            private String currency;

            public Float getValue() {
                return Float.valueOf(value);
            }

            public Currency getCurrency() {
                return Currency.getInstance(currency);
            }
        }
    }

    class PostResponse {
        @SerializedName("Response")
        public List<Item> items;


        public class Item {
            @SerializedName("Id")
            public Id id;
        }

        public class Id {
            public int id;
        }
    }

    class PostRequest {

        public Amount amount;
        @SerializedName("counterparty_alias")
        public CounterpartyAlias counterparty_alias;
        public String description;

    }

    public class Amount {
        public String value;
        public String currency;
    }

    public class CounterpartyAlias {
        public String type = "IBAN";
        public String value;
        public String name;
    }

}
