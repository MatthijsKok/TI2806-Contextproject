package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import java8.util.concurrent.CompletableFuture;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InstallationService {
    @GET("/installation/{id}")
    CompletableFuture<Installation> getInstallation(@Path("id") Integer id);

//    @POST("/installation")
//    CompletableFuture<> createInstallation();

//    @POST
}

class Installation {
    String id;
}

