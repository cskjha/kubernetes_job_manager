package com.vmware.lambda.provider.scaler.core;

import java.util.List;

import com.vmware.lambda.provider.scaler.core.States.ExecutionState;


/**
 * Finds the current state of functions which needs to be executed
 */
public interface PendingFinder {

    List<ExecutionState> getCurrentStates();
}
