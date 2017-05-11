package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import retrofit2.http.Body;
import retrofit2.http.GET;
import java8.util.concurrent.CompletableFuture;
import retrofit2.http.POST;


/**
 * Created by Richard-HP on 10/05/2017.
 */

public interface GetTransactionsService {
    @GET("//v1//user//2002//monetary-account//2021//payment")
    CompletableFuture<InstallationService.CreateResponse> GetTransactions();

  //  @POST("/v1/user/1/monetary-account/11/payment")
   // CompletableFuture<InstallationService.CreateResponse> createPayment(@Body t);

    class CreateResponse{
        @SerializedName("Response")
        public List<bunqtransaction> items;

        public class bunqtransaction{
            @SerializedName("created")
            String created;
            @SerializedName("description")
            String description;
            @SerializedName("amount\\value")
            String value;

        }



        public class payment{
            public amount amount;
            public counterparty_alias counterparty_alias;
            public String description;
        }

        public class amount{
            public String value;
            public String currency;
        }

        public class counterparty_alias{
            public String type = "IBAN";
            public String value;
            public String name;
        }

        class getJson{

        }
    }

}
