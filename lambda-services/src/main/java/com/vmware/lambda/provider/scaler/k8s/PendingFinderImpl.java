package com.vmware.lambda.provider.scaler.k8s;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.api.controller.LambdaControllerServiceImpl;
import com.vmware.lambda.provider.api.dto.EventDto;
import com.vmware.lambda.provider.api.resource.LambdaControllerService;
import com.vmware.lambda.provider.scaler.core.PendingFinder;
import com.vmware.lambda.provider.scaler.core.States.ExecutionState;


/**
 * Provides the current state.
 */
@Component
public class PendingFinderImpl implements PendingFinder {

    @Autowired
    private LambdaControllerServiceImpl lambdaCtrlSvc;

    @Override
    public List<ExecutionState> getCurrentStates() {
        long time = System.currentTimeMillis();
        List<ExecutionState> states = new ArrayList<>();
        List<EventDto> eventByStatusAndValidFrom = lambdaCtrlSvc.findEventByStatusAndValidFrom(EventDto.IN_QUEUE, time);
        Map<String, Long> nqueCnt = eventByStatusAndValidFrom.stream().collect(groupingBy(evt -> Util.qualifier(evt.getApp(), evt.getFunction()), counting()));
        nqueCnt.forEach(toExecutionState(time, states, EventDto.IN_QUEUE));
        return states;
    }

    private BiConsumer<String, Long> toExecutionState(long time, List<ExecutionState> states, String status) {
        return (k, v) -> {
            ExecutionState state = new ExecutionState();
            state.setStatus(status);
            state.setAsOf(time);
            state.setQualifier(k);
            state.setCount(v.intValue());
            states.add(state);
        };
    }
}
