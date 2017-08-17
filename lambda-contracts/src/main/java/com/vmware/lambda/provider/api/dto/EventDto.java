package com.vmware.lambda.provider.api.dto;

/**
 * Created by amkumar on 16/8/2017.
 *
 * Represents a trigger for which the function needs to executed.
 */
public class EventDto {

    public static final String IN_QUEUE = "NQUE", SCHEDULED = "PICK" ,FAILED = "FAIL", DONE = "DONE" ,
            IN_PROGRESS = "BQUE", TIME_OUT = "TIMEOUT";

    public static final int RETRY_LIMIT = 3;

    private String function, app;

    private String status, owner, id;

    private long validFrom, lastUpdatedOn;

    private String payload, responseBody;

    private String configs;

    private int version, priority;

    public EventDto() {
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(long lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "EventDto{" +
                "function='" + function + '\'' +
                ", app='" + app + '\'' +
                ", status='" + status + '\'' +
                ", owner='" + owner + '\'' +
                ", id='" + id + '\'' +
                ", validFrom=" + validFrom +
                ", lastUpdatedOn=" + lastUpdatedOn +
                ", payload='" + payload + '\'' +
                ", responseBody='" + responseBody + '\'' +
                ", configs='" + configs + '\'' +
                ", version=" + version +
                ", priority=" + priority +
                '}';
    }
}
