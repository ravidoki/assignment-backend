package com.gler.assignment.repository;

import com.gler.assignment.model.ForecastRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for ForecastRecord providing CRUD operations.
 */
public interface ForecastRecordRepository extends JpaRepository<ForecastRecord, Long> {
}
