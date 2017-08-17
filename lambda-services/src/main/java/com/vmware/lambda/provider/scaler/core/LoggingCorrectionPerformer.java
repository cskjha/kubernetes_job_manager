package com.vmware.lambda.provider.scaler.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.lambda.provider.scaler.core.States.Correction;

public class LoggingCorrectionPerformer implements CorrectionPerformer {

    private Logger log = LoggerFactory.getLogger(LoggingCorrectionPerformer.class);

    @Override
    public Boolean performCorrectiveAction(Correction correction) {
        log.info("Performing corrective action for {}",correction);
        return true;
    }
}
