package com.vmware.lambda.watcher;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Created by amkumar on 16/8/2017.
 */
@Path("")
public interface WatcherService {

    @GET
    @Path("/input")
    String fetchInput();

    @POST
    @Path("/done")
    String completedSuccessfully(String payload);

    @POST
    @Path("/fail")
    String processingFailed(String payload);

    @GET
    @Path("/input")
    String fetchInputAsync();

    @POST
    @Path("/done")
    String completedSuccessfullyAsync(String payload);

    @POST
    @Path("/fail")
    String processingFailedAsync(String payload);

}
