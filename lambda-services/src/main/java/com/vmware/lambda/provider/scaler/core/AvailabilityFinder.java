package com.vmware.lambda.provider.scaler.core;

import java.util.List;

import com.vmware.lambda.provider.scaler.core.States.Availability;


/**
 * Implementation should find out availability based on the resources (capacity) available at the moment
 * and max permitted parallel executions
 */
public interface AvailabilityFinder {

    List<Availability> getAvailabilityList();

    Availability initialAvailability(String qualifier);
}
