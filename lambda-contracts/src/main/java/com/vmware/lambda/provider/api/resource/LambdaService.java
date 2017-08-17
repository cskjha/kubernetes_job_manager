package com.vmware.lambda.provider.api.resource;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.vmware.lambda.provider.api.dto.AppDto;
import com.vmware.lambda.provider.api.dto.CronScheduleDto;
import com.vmware.lambda.provider.api.dto.EventDto;
import com.vmware.lambda.provider.api.dto.FunktionDto;

/**
 * Created by amkumar on 16/8/2017.
 */
@Path("/lambda/api/v1")
public interface LambdaService {

    int DEFAULT_LAMBDA_MEMORY = 1000;

    @GET
    @Path("/app")
    List<AppDto> listAllApps();

    @POST
    @Path("/app")
    AppDto newApp(AppDto appDto);

    @PUT
    @Path("/app")
    AppDto updateApp(AppDto app);

    @POST
    @Path("/function/{app}")
    FunktionDto newFunction(@PathParam("app") String appName, FunktionDto funktion);

    @POST
    @Path("/cronSchedule/{app}/{function}")
    CronScheduleDto newSchedule(@PathParam("app") String app, @PathParam("function") String function, CronScheduleDto cronSchedule);

    @DELETE
    @Path("/schedule/{app}/{function}/{schedule}")
    CronScheduleDto deleteSchedule(@PathParam("app") String app, @PathParam("function") String function, @PathParam("schedule") String schedule);

    @GET
    @Path("/function/{app}")
    List<FunktionDto> listFunctions(@PathParam("app") String appName, @QueryParam("function") String functionName);

    @PUT
    @Path("/function/{app}")
    FunktionDto updateFunction(@PathParam("app") String appName,  FunktionDto funktion);

    @POST
    @Path("/activate/{app}/{function}")
    EventDto newEvent(@PathParam("app") String appName, @PathParam("function") String function, Object payload);

    @POST
    @Path("/activate/{app}/{function}/{priority}")
    EventDto newEventWithPriority(@PathParam("app") String appName, @PathParam("function") String function, @PathParam("priority") Integer priority, Object payload);

    @GET
    @Path("/activate/{id}")
    EventDto findEventById(@PathParam("id") String id);
}
