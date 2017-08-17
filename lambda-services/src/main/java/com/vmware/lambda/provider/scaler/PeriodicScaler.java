package com.vmware.lambda.provider.scaler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vmware.lambda.provider.scaler.core.States.Availability;
import com.vmware.lambda.provider.scaler.core.States.Correction;
import com.vmware.lambda.provider.scaler.core.States.ExecutionState;
import com.vmware.lambda.provider.scaler.core.VolatileStateManager;
import com.vmware.lambda.provider.scaler.k8s.CorrectionFinderImpl;
import com.vmware.lambda.provider.scaler.k8s.KubeJobCorrectionPerformer;

/**
 * Created by atulj on 8/16/2017.
 */
@Component
public class PeriodicScaler {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicScaler.class);

    @Autowired
    private VolatileStateManager stateManager;

    @Autowired
    private CorrectionFinderImpl correctionFinder;

    @Autowired
    private KubeJobCorrectionPerformer correctionPerformer;

    @Scheduled(cron="0 0/5 * * * ?")
    public void run() {
        logger.info("Executing new run of scaler");
        List<Availability> availabilities = stateManager.getAvailabilityList();
        List<ExecutionState> pendings = stateManager.getCurrentStates();
        Map<String, Availability> availabilityMap = new HashMap<>();
        availabilities.forEach(av -> availabilityMap.put(av.getQualifier(), av));

        pendings.forEach(pendingState -> {
            Availability availability = availabilityMap.get(pendingState.getQualifier());
            if( availability == null ){
                logger.info("No availability. Initializing flow now");
                availability = stateManager.initialAvailability(pendingState.getQualifier());
            }
            logger.info("Pending queue size for {} is {}",pendingState.getQualifier(), pendingState.getCount());
            logger.info("Occupancy for {} is {}",availability.getQualifier(), availability.getOccupiedCapacity());
            Correction correction = correctionFinder.findCorrection(availability, pendingState);
            if (correction != null) {
                logger.info("Need to execute functions for {} ", correction);
                correctionPerformer.performCorrectiveAction(correction);
            }
        });
        logger.info("scaler execution completed");
    }
}
