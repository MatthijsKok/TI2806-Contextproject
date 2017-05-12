package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.Currency;
import java.util.List;

import retrofit2.http.GET;
import java8.util.concurrent.CompletableFuture;
import retrofit2.http.Path;


/**
 * API for the payment endpoint.
 *
 * @note This is MVP
 *
 * @author Richard
 */
public interface PaymentService {
    @GET("/v1/user/{user}/monetary-account/{account}/payment")
    CompletableFuture<ListResponse> listPayments(@Path("user") int user, @Path("account") int account);

  //  @POST("/v1/user/1/monetary-account/11/payment")
   // CompletableFuture<InstallationService.CreateResponse> createPayment(@Body t);

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
}
