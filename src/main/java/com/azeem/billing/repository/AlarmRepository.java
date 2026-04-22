/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.repository;

import com.azeem.billing.entity.AlarmEntity;
import com.azeem.billing.model.AlarmScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, UUID> {
    List<AlarmEntity> findByBillingPeriod(String billingPeriod);
    List<AlarmEntity> findByBillingPeriodAndAlarmScope(String billingPeriod, AlarmScope scope);
    List<UUID> findBusinessKeysByBillingPeriod(String billingPeriod);
    boolean existsById(UUID id);
}
