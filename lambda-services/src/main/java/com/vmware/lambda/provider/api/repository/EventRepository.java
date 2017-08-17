package com.vmware.lambda.provider.api.repository;


import com.vmware.lambda.provider.api.dto.EventDto;
import com.vmware.lambda.provider.api.model.EventOrm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.function.Function;

public interface EventRepository extends JpaRepository<EventOrm, String> {

    public List<EventOrm> findByStatus(String status);

    @Query("select event from EventOrm  event where event.id = ?1")
    public EventOrm findById(String id);

    @Query("select p from EventOrm p where p.status = :status and p.validFrom <= :validFrom")
    public List<EventOrm> findByStatusAndValidFromLessThan(@Param("status") String status, @Param("validFrom") long validFrom);

    @Query("select p from EventOrm p where p.status = :status and p.appOrm.name = :app and p.function = :function ORDER BY p.priority DESC, p.validFrom ASC")
    public List<EventOrm> findEventByStatusAndAppAndFunctionOrderByValidFromAndPriority(@Param("status") String status, @Param("app") String app, @Param("function") String function);

    public static final Function<EventOrm, EventDto> TO_MODEL = doc -> {
        if (doc == null) {
            return null;
        }
        EventDto model = new EventDto();
        model.setApp(doc.getAppOrm().getName());
        model.setFunction(doc.getFunction());
        model.setValidFrom(doc.getValidFrom());
        model.setPayload(doc.getPayload());
        model.setResponseBody(doc.getResponseBody());
        model.setStatus(doc.getStatus());
        model.setConfigs(doc.getConfigs());
        model.setOwner(doc.getOwner());
        model.setVersion((int) doc.getVersion());
        model.setLastUpdatedOn(doc.getLastUpdatedOn());
        model.setId(doc.getId());
        model.setPriority(doc.getPriority());
        return model;
    };

}
