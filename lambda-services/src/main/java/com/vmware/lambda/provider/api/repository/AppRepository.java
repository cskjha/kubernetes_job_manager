package com.vmware.lambda.provider.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vmware.lambda.provider.api.model.AppOrm;

/**
 * Created by amkumar on 14/8/2017.
 */
public interface AppRepository extends JpaRepository<AppOrm, String> {

    AppOrm findByName(String name);

}
