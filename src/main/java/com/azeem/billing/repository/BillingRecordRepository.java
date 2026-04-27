/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.repository;

import com.azeem.billing.entity.BillingRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


@Repository
public interface BillingRecordRepository extends JpaRepository<BillingRecordEntity, Long> {

    @Query("SELECT DISTINCT b.billingPeriod FROM BillingRecordEntity b ORDER BY b.billingPeriod")
    Page<String> findAllBillingPeriods(Pageable pageable);

    // DB-level distinct departments
    @Query("SELECT DISTINCT b.department FROM BillingRecordEntity b ORDER BY b.department")
    List<String> findDistinctDepartments();

    // DB-level distinct billing periods (non-paged)
    @Query("SELECT DISTINCT b.billingPeriod FROM BillingRecordEntity b ORDER BY b.billingPeriod")
    List<String> findAllDistinctBillingPeriods();

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BillingRecordEntity b WHERE b.billingPeriod = :period")
    boolean existsByBillingPeriod(@Param("period") String period);

    Page<BillingRecordEntity> findAll(Pageable pageable);
    Page<BillingRecordEntity> findByBillingPeriod(String billingPeriod, Pageable pageable);
    List<BillingRecordEntity> findByBillingPeriod(String billingPeriod);
    Page<BillingRecordEntity> findByDepartmentIgnoreCase(String department, Pageable pageable);
}
