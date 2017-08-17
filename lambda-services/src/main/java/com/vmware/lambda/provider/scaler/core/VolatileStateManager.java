package com.vmware.lambda.provider.scaler.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.scaler.core.States.Availability;
import com.vmware.lambda.provider.scaler.core.States.ExecutionState;

@Component
public class VolatileStateManager implements StateManager {

    @Autowired
    private PendingFinder pendingFinder;

    @Autowired
    private AvailabilityFinder availabilityFinder;

    @Override
    public List<ExecutionState> getCurrentStates() {
        return pendingFinder.getCurrentStates();
    }

    @Override
    public List<Availability> getAvailabilityList() {
        return availabilityFinder.getAvailabilityList();
    }

    @Override
    public Availability initialAvailability(String qualifier) {
        return availabilityFinder.initialAvailability(qualifier);
    }
}
