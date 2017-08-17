package com.vmware.lambda.provider.api.dto;

/**
 * Created by amkumar on 16/8/2017.
 *
 */
public class AppDto {
    private String name;
    private String configs;

    public AppDto() {
    }

    public AppDto(String name, String configs) {
        this.name = name;
        this.configs = configs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    @Override
    public String toString() {
        return "AppDto{" +
                "name='" + name + '\'' +
                ", configs='" + configs + '\'' +
                '}';
    }
}
