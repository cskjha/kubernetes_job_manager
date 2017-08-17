package com.vmware.lambda.provider.scaler.k8s;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.api.controller.LambdaControllerServiceImpl;
import com.vmware.lambda.provider.api.dto.EventDto;
import com.vmware.lambda.provider.scaler.core.AvailabilityFinder;
import com.vmware.lambda.provider.scaler.core.States.Availability;


/**
 * Bare minimum implementation to start with
 * <p>
 * Finds out the executing functions and creates availability out of it
 */
@Component
public class AvailabilityFinderImpl implements AvailabilityFinder {

    // max parallelism for qualifier
    static final int MAX_CAPACITY_PER_FUNC = 3;

    @Autowired
    private LambdaControllerServiceImpl ctrlSvc;

    @Override
    public List<Availability> getAvailabilityList() {
        List<EventDto> eventByStatusAndValidFrom = ctrlSvc.findEventByStatusAndValidFrom(EventDto.IN_PROGRESS, null);
        return eventByStatusAndValidFrom.stream().collect(groupingBy(evt -> Util.qualifier(evt.getApp(), evt.getFunction()), counting()))
                .entrySet().stream().map(entry -> {
                    Availability availability = new Availability();
                    availability.setQualifier(entry.getKey());
                    availability.setOccupiedCapacity(entry.getValue().intValue());
                    availability.setMaxCapacity(MAX_CAPACITY_PER_FUNC);
                    return availability;
                }).collect(Collectors.toList());
    }

    @Override
    public Availability initialAvailability(String qualifier) {
        Availability availability = new Availability();
        availability.setQualifier(qualifier);
        availability.setMaxCapacity(MAX_CAPACITY_PER_FUNC);
        availability.setOccupiedCapacity(0);
        return availability;
    }
}
