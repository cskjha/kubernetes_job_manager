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
 * Represents a Lambda or Function which is a stateless, finite business logic encapsulated as a docker image.
 *
 * This inherits the configs from {@link AppOrm} and overrides them.
 *
 * Implementation Notes : Named as FunktionOrm instead of Function as Function might conflict with java util Function
 */
@Entity
@Table(name = "function")
public class FunktionOrm {

    private String name, image;

    private String configs;

    private String envs;

    private int timeout, memory, maxRetryCount, priority;

    private Boolean disable;

    private AppOrm appOrm;

    @Id
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "image")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Column(name = "configs")
    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    @Column(name = "envs")
    public String getEnvs() {
        return envs;
    }

    public void setEnvs(String envs) {
        this.envs = envs;
    }

    @Column(name = "timeout")
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Column(name = "memory")
    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Column(name = "max_retry_count")
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Column(name = "disabled")
    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
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
