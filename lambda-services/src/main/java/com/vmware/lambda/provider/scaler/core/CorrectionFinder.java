package com.vmware.lambda.provider.scaler.core;


import com.vmware.lambda.provider.scaler.core.States.Availability;
import com.vmware.lambda.provider.scaler.core.States.Correction;
import com.vmware.lambda.provider.scaler.core.States.ExecutionState;

public interface CorrectionFinder {

    Correction findCorrection(Availability availability, ExecutionState currentState);
}
