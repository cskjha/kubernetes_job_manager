package com.vmware.lambda.provider.scaler.k8s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.scaler.core.CorrectionFinder;
import com.vmware.lambda.provider.scaler.core.States.Availability;
import com.vmware.lambda.provider.scaler.core.States.Correction;
import com.vmware.lambda.provider.scaler.core.States.ExecutionState;


/**
 * Simplest possible implementation which performs correction when no functions are running.
 * <p>
 * This is because when functions are running, it also means watcher is also running and
 * watcher is assumed to have ability to loop through pending functions one by one.
 */
@Component
public class CorrectionFinderImpl implements CorrectionFinder {

    private Logger log = LoggerFactory.getLogger(CorrectionFinderImpl.class);

    @Override
    public Correction findCorrection(Availability availability, ExecutionState pendingState) {
        if (availability.hasCapacity() && pendingState.getCount() > 0) {
            int count = pendingState.getCount();
            Correction correction = new Correction();
            correction.setParallelism(availability.getMaxCapacity() < count ? availability.getMaxCapacity() : count);
            correction.setCompletions(count);
            correction.setQualifier(availability.getQualifier());
            log.info("Required correction is {}",correction);
            return correction;
        } else {
            int capacity = availability.getMaxCapacity() - availability.getOccupiedCapacity();
            log.info("No corrections needed for {} as capacity available is {} and pending queue size is {}", availability.getQualifier(), capacity, pendingState.getCount());
            return null;
        }
    }
}
