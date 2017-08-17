package com.vmware.lambda.provider.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by amkumar on 14/8/2017.
 *
 * Represents an application which can contain one or more functions
 */
@Entity
@Table(name = "app")
public class AppOrm {

    private String name;
    private String configs;

    @Id
    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "configs")
    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    public void setName(String name) {
        this.name = name;
    }
}
