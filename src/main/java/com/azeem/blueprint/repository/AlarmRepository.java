/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.repository;

import com.azeem.blueprint.entity.AlarmEntity;
import com.azeem.blueprint.model.alarm.AlarmScope;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, UUID> {
  List<AlarmEntity> findByDatasetIdAndBillingPeriod(UUID datasetId, String billingPeriod);

  List<AlarmEntity> findByDatasetIdAndBillingPeriodAndAlarmScope(
      UUID datasetId, String billingPeriod, AlarmScope scope);

  boolean existsByDatasetIdAndId(UUID datasetId, UUID id);

  @Query(
      """
          SELECT a.businessKey
          FROM AlarmEntity a
          WHERE a.dataset.id = :datasetId AND a.billingPeriod = :billingPeriod
          """)
  List<UUID> findBusinessKeysByDatasetIdAndBillingPeriod(
      @Param("datasetId") UUID datasetId, @Param("billingPeriod") String billingPeriod);
}
