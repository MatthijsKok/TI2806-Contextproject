package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.http.GET;
import java8.util.concurrent.CompletableFuture;


/**
 * Created by Richard-HP on 10/05/2017.
 */

public interface GetTransactionsService {
    @GET("/v1/user/2002/monetary-account/2021/payment")
    CompletableFuture<CreateResponse> GetTransactions();

  //  @POST("/v1/user/1/monetary-account/11/payment")
   // CompletableFuture<InstallationService.CreateResponse> createPayment(@Body t);

    class CreateResponse{
        @SerializedName("Response")
        public List<element> items;

        public class element{
            @SerializedName("Payment")
            public Payment payment;
        }

        public class Payment{
            @SerializedName("created")
            public String created;
            @SerializedName("description")
            public String description;
            @SerializedName("amount")
            public amount amount;
            @SerializedName("counterparty_alias")
            public CounterParty counterParty;
        }


        public class CounterParty{
            @SerializedName("iban")
            public String iban;
            @SerializedName("display_name")
            public String name;
        }
/*
        public class payment{
            public amount amount;
            public counterparty_alias counterparty_alias;
            public String description;
        }
*/
        public class amount{
            @SerializedName("value")
            public String value;
            @SerializedName("currency")
            public String currency;
        }

        public class counterparty_alias {
            public String type = "IBAN";
            public String value;
            public String name;
        }
    }

}
