package com.vmware.lambda.provider.scaler.core;

import java.util.List;

import com.vmware.lambda.provider.scaler.core.States.Availability;
import com.vmware.lambda.provider.scaler.core.States.ExecutionState;

public interface StateManager {

    List<ExecutionState> getCurrentStates();

    List<Availability> getAvailabilityList();

    Availability initialAvailability(String qualifier);
}
