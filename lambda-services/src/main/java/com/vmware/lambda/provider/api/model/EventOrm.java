package com.vmware.lambda.provider.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by guptavishal on 14/8/2017.
 *
 * Represents a trigger for which the function needs to executed.
 */
@Entity
@Table(name = "event")
public class EventOrm {

    public static final String IN_QUEUE = "NQUE", SCHEDULED = "PICK" ,FAILED = "FAIL", DONE = "DONE" ,
            IN_PROGRESS = "BQUE", TIME_OUT = "TIMEOUT";

    public static final int RETRY_LIMIT = 3;

    private String function;

    private String status, owner, id;

    private long validFrom, lastUpdatedOn;

    private String payload, responseBody;

    private String configs;

    private int version, priority;

    public int retry;
    public String retryReason;

    private AppOrm appOrm;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "function")
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "owner")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Column(name = "valid_from")
    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    @Column(name = "last_updated")
    public long getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(long lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    @Column(name = "payload")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Column(name = "response_body")
    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Column(name = "configs")
    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    @Column(name = "version")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Column(name = "retry")
    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
    @Column(name = "retryreason")
    public String getRetryReason() {
        return retryReason;
    }

    public void setRetryReason(String retryReason) {
        this.retryReason = retryReason;
    }

    @ManyToOne
    @JoinColumn(name ="app", referencedColumnName = "name")
    public AppOrm getAppOrm() {
        return appOrm;
    }

    public void setAppOrm(AppOrm appOrm) {
        this.appOrm = appOrm;
    }
}
