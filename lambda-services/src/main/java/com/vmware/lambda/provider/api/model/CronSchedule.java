package com.vmware.lambda.provider.api.model;

import javax.validation.constraints.NotNull;

/**
 * Created by amkumar on 16/8/2017.
 *
 * Represents a trigger for which the function needs to executed.
 */
public class CronSchedule {

    @NotNull
    private String name,  payload, cron;

    private String function, app, id;

    public CronSchedule() {
    }

    public CronSchedule(String name, String payload, String cron, String function, String app, String id) {
        this.name = name;
        this.payload = payload;
        this.cron = cron;
        this.function = function;
        this.app = app;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
