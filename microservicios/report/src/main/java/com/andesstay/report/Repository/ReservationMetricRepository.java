package com.andesstay.report.Repository;

import com.andesstay.report.Model.ReservationMetric;
import com.andesstay.report.DTO.TopUnitResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationMetricRepository extends JpaRepository<ReservationMetric, Long> {

    @Query("SELECT COUNT(rm) FROM ReservationMetric rm WHERE rm.eventTimestamp >= :since")
    long countReservationsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(rm) FROM ReservationMetric rm WHERE rm.status = 'EN_ESTADIA'")
    long countActiveOccupancy();

    @Query("SELECT new com.andesstay.report.DTO.TopUnitResponseDTO(rm.unitId, COUNT(rm)) " +
            "FROM ReservationMetric rm WHERE rm.eventTimestamp >= :since " +
            "GROUP BY rm.unitId ORDER BY COUNT(rm) DESC")
    List<TopUnitResponseDTO> findTopUnitsSince(@Param("since") LocalDateTime since);

    List<ReservationMetric> findAllByEventTimestampGreaterThanEqual(LocalDateTime since);
}