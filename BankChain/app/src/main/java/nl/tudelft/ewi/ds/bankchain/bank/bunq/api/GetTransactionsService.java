package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import nl.tudelft.ewi.ds.bankchain.bank.Transaction;
import retrofit2.http.Body;
import retrofit2.http.GET;
import java8.util.concurrent.CompletableFuture;


/**
 * Created by Richard-HP on 10/05/2017.
 */

public interface GetTransactionsService {
    @GET("//v1//user//2002//monetary-account//2021//payment")
    CompletableFuture<InstallationService.CreateResponse> GetTransactions();

    class CreateResponse{
        @SerializedName("Response")
        public List<Payment> items;

        public class Payment{
            @SerializedName("created")
            String created;
            @SerializedName("description")
            String description;
            @SerializedName("amount\\value")
            String value;

        }



    }

}
