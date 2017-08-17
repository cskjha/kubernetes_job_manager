package com.vmware.lambda.provider.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.lambda.provider.api.dto.AppDto;
import com.vmware.lambda.provider.api.dto.CronScheduleDto;
import com.vmware.lambda.provider.api.dto.EventDto;
import com.vmware.lambda.provider.api.dto.FunktionDto;
import com.vmware.lambda.provider.api.model.AppOrm;
import com.vmware.lambda.provider.api.model.EventOrm;
import com.vmware.lambda.provider.api.model.FunktionOrm;
import com.vmware.lambda.provider.api.repository.AppRepository;
import com.vmware.lambda.provider.api.repository.EventRepository;
import com.vmware.lambda.provider.api.repository.FunctionRepository;
import com.vmware.lambda.provider.api.resource.LambdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by amkumar on 16/8/2017.
 */
@Controller("lambdaService")
@RequestMapping(value = "/lambda/api/v1", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8")
public class LambdaServiceImpl implements LambdaService{

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private EventRepository eventRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/app")
    public List<AppDto> listAllApps() {
        return appRepository.findAll().stream().map(appOrm -> objectMapper.convertValue(appOrm, AppDto.class)).collect(Collectors.toList());
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/app", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppDto newApp(@RequestBody AppDto appDto){
        AppOrm appOrmOrm = objectMapper.convertValue(appDto, AppOrm.class);
        appRepository.save(appOrmOrm);
        return appDto;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/app", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppDto updateApp(@RequestBody AppDto app) {
        AppOrm appOrmByName = appRepository.findOne(app.getName());
        appOrmByName.setName(app.getName());
        appOrmByName.setConfigs(app.getConfigs());
        appRepository.save(appOrmByName);
        return app;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/function/{app}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FunktionDto newFunction(@PathVariable("app") String appName, @RequestBody FunktionDto funktion) {
        FunktionOrm funktionOrm = getFunctionOrmFromDto(funktion);
        funktionOrm.setAppOrm(appRepository.findOne(appName));
        functionRepository.save(funktionOrm);
       return funktion;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/cronSchedule/{app}/{function}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CronScheduleDto newSchedule(@PathVariable("app") String app, @PathVariable("function") String function, CronScheduleDto cronSchedule) {
        return null;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/schedule/{app}/{function}/{schedule}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CronScheduleDto deleteSchedule(@PathVariable("app") String app, @PathVariable("function") String function, String schedule) {
        return null;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/function/{app}")
    public List<FunktionDto> listFunctions(@PathVariable("app") String appName, @RequestParam("function") String functionName) {
       List<FunktionDto> funktionDtos = new ArrayList<>();
       List<FunktionOrm> funktionOrms = functionRepository.findByAppNameAndFunctionName(appName, functionName);
       for(FunktionOrm funktionOrm : funktionOrms) {
           funktionDtos.add(getFunctionDtoFromOrm(funktionOrm));
       }
       return  funktionDtos;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/function/{app}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FunktionDto updateFunction(@PathVariable("app") String appName, @RequestBody FunktionDto funktion) {
        FunktionDto funktionDto = new FunktionDto();
        List<FunktionOrm> functionOrms = functionRepository.findByAppNameAndFunctionName(appName, funktion.getName());
        FunktionOrm functionOrm = null;
        if(functionOrms !=null && functionOrms.size() > 0) {
            functionOrm = updateFunktionOrm(funktion, functionOrms.get(0));
            functionRepository.save(functionOrm);
        }
        return getFunctionDtoFromOrm(functionOrm);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/activate/{app}/{function}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EventDto newEvent(@PathVariable("app") String appName, @PathVariable("function") String function, @RequestBody Object payload) {
        EventOrm eventOrm = new EventOrm();
        eventOrm.setStatus(EventDto.IN_QUEUE);
        eventOrm.setValidFrom(System.currentTimeMillis());
        eventOrm.setFunction(function);
        eventOrm.setAppOrm(appRepository.findByName(appName));
        eventOrm.setPayload(payload.toString());
        eventOrm.setId(appName+function+System.currentTimeMillis());
        eventRepository.save(eventOrm);
        return getEventDtoFromOrm(eventOrm);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/activate/{app}/{function}/{priority}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EventDto newEventWithPriority(@PathVariable("app") String appName, @PathVariable("function") String function,
                                         @PathVariable("priority") Integer priority, @RequestBody Object payload) {
        EventOrm eventOrm = new EventOrm();
        eventOrm.setStatus(EventDto.IN_QUEUE);
        eventOrm.setValidFrom(System.currentTimeMillis());
        eventOrm.setFunction(function);
        eventOrm.setAppOrm(appRepository.findByName(appName));
        eventOrm.setPayload(payload.toString());
        eventOrm.setPriority(priority == null ? 0 : priority);
        eventOrm.setId(appName+function+System.currentTimeMillis());
        eventRepository.save(eventOrm);
        return getEventDtoFromOrm(eventOrm);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/activate/{id}")
    public EventDto findEventById(@PathVariable("id") String id) {
        EventOrm eventOrm = eventRepository.findById(id);
        return getEventDtoFromOrm(eventOrm);
    }

    private EventDto getEventDtoFromOrm(EventOrm eventOrm) {
        EventDto eventDto = new EventDto();
        eventDto.setApp(eventOrm.getAppOrm().getName());
        eventDto.setConfigs(eventOrm.getConfigs());
        eventDto.setFunction(eventOrm.getFunction());
        eventDto.setId(eventOrm.getId());
        eventDto.setLastUpdatedOn(eventOrm.getLastUpdatedOn());
        eventDto.setOwner(eventOrm.getOwner());
        eventDto.setPriority(eventOrm.getPriority());
        eventDto.setResponseBody(eventOrm.getResponseBody());
        eventDto.setPayload(eventOrm.getPayload());
        eventDto.setStatus(eventOrm.getStatus());
        eventDto.setValidFrom(eventOrm.getValidFrom());
        eventDto.setVersion(eventOrm.getVersion());
        return eventDto;
    }
    private FunktionOrm getFunctionOrmFromDto(FunktionDto funktion) {
        FunktionOrm funktionOrm = new FunktionOrm();
        funktionOrm.setImage(funktion.getImage());
        funktionOrm.setName(funktion.getName());
        funktionOrm.setConfigs(funktion.getConfigs());
        funktionOrm.setEnvs(funktion.getEnvs());
        funktionOrm.setTimeout(funktion.getTimeout());
        funktionOrm.setMemory(funktion.getMemory());
        funktionOrm.setMaxRetryCount(funktion.getMaxRetryCount());
        funktionOrm.setPriority(funktion.getPriority());
        funktionOrm.setDisable(funktion.getDisable());
        return funktionOrm;
    }

    private FunktionOrm updateFunktionOrm(FunktionDto funktion, FunktionOrm funktionOrm) {
        funktionOrm.setImage(funktion.getImage());
        funktionOrm.setName(funktion.getName());
        funktionOrm.setConfigs(funktion.getConfigs());
        funktionOrm.setEnvs(funktion.getEnvs());
        funktionOrm.setTimeout(funktion.getTimeout());
        funktionOrm.setMemory(funktion.getMemory());
        funktionOrm.setMaxRetryCount(funktion.getMaxRetryCount());
        funktionOrm.setPriority(funktion.getPriority());
        funktionOrm.setDisable(funktion.getDisable());
        return funktionOrm;
    }
    private FunktionDto getFunctionDtoFromOrm(FunktionOrm funktionOrm) {
        FunktionDto funktionDto = new FunktionDto();
        if(funktionOrm == null)
            return funktionDto;
        funktionDto.setImage(funktionOrm.getImage());
        funktionDto.setName(funktionOrm.getName());
        funktionDto.setConfigs(funktionOrm.getConfigs());
        funktionDto.setEnvs(funktionOrm.getEnvs());
        funktionDto.setTimeout(funktionOrm.getTimeout());
        funktionDto.setMemory(funktionOrm.getMemory());
        funktionDto.setMaxRetryCount(funktionOrm.getMaxRetryCount());
        funktionDto.setPriority(funktionOrm.getPriority());
        funktionDto.setDisable(funktionOrm.getDisable());
        return funktionDto;
    }
}
