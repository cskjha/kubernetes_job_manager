package com.vmware.lambda.provider.scaler.k8s;

public class Util {

    private static String SEPARATOR = ":";

    static String qualifier(String app, String fn) {
        return app +SEPARATOR + fn;
    }

    static String appFromQualifier(String qualifier){
        return qualifier.split(SEPARATOR)[0];
    }

    static String functionFromQualifier(String qualifier){
        return qualifier.split(SEPARATOR)[1];
    }

}
