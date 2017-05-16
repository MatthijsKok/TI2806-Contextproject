package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import java8.util.concurrent.CompletableFuture;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by Richard-HP on 15/05/2017.
 */

public interface UserService {
    @GET("/v1/user")
    CompletableFuture<ListResponse> getUsers();

     class ListResponse{
         @SerializedName("Response")
         public List<Item> items;

         public class Item {
             @SerializedName("UserCompany")
             public UserCompany user;
         }

         public class UserCompany {
             @SerializedName("id")
             public int id;

             @SerializedName("name")
             public String name;
         }
     }

}
