package com.vmware.lambda.provider.api.dto;

/**
 * Created by amkumar on 16/8/2017.
 *
 * Represents a Lambda or Function which is a stateless, finite business logic encapsulated as a docker image.
 *
 * This inherits the configs from {@link AppDto} and overrides them.
 *
 * Implementation Notes : Named as FunktionOrm instead of Function as Function might conflict with java util Function
 */
public class FunktionDto {

    private String name, image;

    private String configs;
    private String envs;

    private int timeout,memory, maxRetryCount, priority;

    private Boolean disable;

    public FunktionDto() {
    }

    public FunktionDto(String name, String image, String configs, String envs, int timeout, int memory, int maxRetryCount, int priority, Boolean disable) {
        this.name = name;
        this.image = image;
        this.configs = configs;
        this.envs = envs;
        this.timeout = timeout;
        this.memory = memory;
        this.maxRetryCount = maxRetryCount;
        this.priority = priority;
        this.disable = disable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    public String getEnvs() {
        return envs;
    }

    public void setEnvs(String envs) {
        this.envs = envs;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    @Override
    public String toString() {
        return "FunktionDto{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", configs='" + configs + '\'' +
                ", envs='" + envs + '\'' +
                ", timeout=" + timeout +
                ", memory=" + memory +
                ", maxRetryCount=" + maxRetryCount +
                ", priority=" + priority +
                ", disable=" + disable +
                '}';
    }
}
