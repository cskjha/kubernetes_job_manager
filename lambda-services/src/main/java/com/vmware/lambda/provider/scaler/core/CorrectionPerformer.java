package com.vmware.lambda.provider.scaler.core;

import com.vmware.lambda.provider.scaler.core.States.Correction;


public interface CorrectionPerformer {

    Boolean performCorrectiveAction(Correction correction);
}
