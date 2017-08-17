package com.vmware.lambda.provider.api.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vmware.lambda.provider.api.model.FunktionOrm;

/**
 * Created by amkumar on 16/8/2017.
 */
public interface FunctionRepository extends JpaRepository<FunktionOrm, Long> {

//    public List<FunktionOrm> findByAppLinkAndName(String appLink, String name);

//    public List<FunktionOrm> findByAppLink(String appLink);

    @Query("Select funOrm from FunktionOrm funOrm where funOrm.appOrm.name=?1 and funOrm.name=?2")
    List<FunktionOrm> findByAppNameAndFunctionName(String appName, String functionName);
}
