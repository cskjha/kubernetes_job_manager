package com.vmware.lambda.provider.api.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.vmware.lambda.provider.api.dto.EventDto;

/**
 * Created by amkumar on 16/8/2017.
 *
 * Non public facing APIs to be used by scaler and invoker/watcher of lambda infrastructure.
 *
 */
@Path("/lambda/api/v1/executable")
 public interface LambdaControllerService {

    @GET
    @Path("/event/count/byStatus")
    Map<String,Integer> countOfEventByStatus(@QueryParam("status") String status);

    @GET
    @Path("/event/findBy")
    List<EventDto> findEventByStatusAndValidFrom(String status, @QueryParam("createdAfter") Long createdAfter);

    @GET
    @Path("/event/byPriority")
    EventDto findExecutableEvent(@QueryParam("app") String appName, @QueryParam("function") String functionName);

    @PUT
    @Path("/event/{id}")
    EventDto updateEvent(@PathParam("id") String id, EventDto event);
}
