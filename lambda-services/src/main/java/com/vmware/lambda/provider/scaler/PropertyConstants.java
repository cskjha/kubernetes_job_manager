package com.vmware.lambda.provider.scaler;

import io.fabric8.kubernetes.api.model.IntOrString;

public final class PropertyConstants {

    private PropertyConstants(){
    }

    public static final String LAMBDA_SVC_URI = "LAMBDA_SVC_URI";
    public static final String LAMBDA_FUNCTION_NAME = "LAMBDA_FUNCTION_NAME";
    public static final String LAMBDA_APP_NAME = "LAMBDA_APP_NAME";
    public static final String IMAGE_PULL_SECRET = "IMAGE_PULL_SECRET";
    public static final String CONTAINER_VOLUME_MOUNT_PATH = "/var/log/hostVarLogMount";
    public static final String JOB_VOLUME_MOUNT_PATH = "/var/log";
    public static final String VOLUME_MOUNT_NAME = "lambda-logs";
    public static final int    K8S_MAX_NAME_LENGTH = 63;
    public static final String K8S_JOB_HEALTH_URL = "/health";
    public static final IntOrString WATCHER_PORT = new IntOrString(8080);
}
