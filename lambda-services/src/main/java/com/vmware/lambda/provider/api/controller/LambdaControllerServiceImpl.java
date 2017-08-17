package com.vmware.lambda.provider.api.controller;

import static java.util.Collections.singletonMap;

import static com.vmware.lambda.provider.api.dto.EventDto.IN_QUEUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.lambda.provider.api.dto.EventDto;
import com.vmware.lambda.provider.api.model.EventOrm;
import com.vmware.lambda.provider.api.repository.AppRepository;
import com.vmware.lambda.provider.api.repository.EventRepository;
import com.vmware.lambda.provider.api.repository.FunctionRepository;
import com.vmware.lambda.provider.api.resource.LambdaControllerService;

@Controller("lambdaControllerService")
@RequestMapping(value = "/lambda/api/v1/event")
public class LambdaControllerServiceImpl implements LambdaControllerService {

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/count/byStatus")
    public Map<String, Integer> countOfEventByStatus(@DefaultValue(IN_QUEUE) @QueryParam("status") String status) {
        if(status == null) {
            return Collections.emptyMap();
        }
        List<EventOrm> byStatus = eventRepository.findByStatus(status);
        return singletonMap(status, byStatus.size());
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/findBy")
    public List<EventDto> findEventByStatusAndValidFrom(@DefaultValue(IN_QUEUE) @QueryParam("status") String status,
                                                        @QueryParam("createdBefore") Long createdBefore) {
        List<EventDto> result = new ArrayList<>();
        if (createdBefore == null || createdBefore < 1) {
            eventRepository.findByStatus(status).forEach(docs -> result.add(eventRepository.TO_MODEL.apply(docs)));
        } else {
            eventRepository.findByStatusAndValidFromLessThan(status, createdBefore).forEach(docs -> result.add(eventRepository.TO_MODEL.apply(docs)));
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/byPriority")
    public EventDto findExecutableEvent(@RequestParam("app") String appName, @RequestParam("function") String functionName) {
        List<EventOrm> events = eventRepository.findEventByStatusAndAppAndFunctionOrderByValidFromAndPriority(EventOrm.IN_QUEUE, appName, functionName);
        return eventRepository.TO_MODEL.apply(events.get(0));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public EventDto updateEvent(@PathVariable("id") String id, @RequestBody EventDto event) {
        EventOrm bySelfLink = eventRepository.findById(id);
        return eventRepository.TO_MODEL.apply(updateEvent(event.getStatus(), event, bySelfLink));
    }

    private EventOrm updateEvent(String status, EventDto event, EventOrm bySelfLink) {
        bySelfLink.setStatus(status);
        bySelfLink.setVersion(event.getVersion());
        bySelfLink.setLastUpdatedOn(System.currentTimeMillis());
        bySelfLink.setOwner(event.getOwner());
        int nextRetryCnt = bySelfLink.getRetry() + 1;
        if (EventDto.RETRY_LIMIT > nextRetryCnt && (EventDto.FAILED.equals(status) || EventDto.TIME_OUT.equals(status))) {
            bySelfLink.setRetry(nextRetryCnt);
            bySelfLink.setRetryReason(status);
            bySelfLink.setStatus(EventDto.IN_QUEUE);
            bySelfLink.setValidFrom(System.currentTimeMillis());
        }
        if (event.getResponseBody() != null) {
            bySelfLink.setResponseBody(event.getResponseBody());
        }
        eventRepository.save(bySelfLink);
        return bySelfLink;
    }

    private EventOrm getEventById(EventDto eventDto) throws Exception {
        EventOrm event = eventRepository.getOne(eventDto.getId());
        if (event == null) {
            throw new Exception("No event with id exists : " + event.getId());
        } else {
            return event;
        }
    }
}
