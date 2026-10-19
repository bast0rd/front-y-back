package com.andesstay.catalog.Repository;

import com.andesstay.catalog.Model.Unit;
import com.andesstay.catalog.Model.UnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByStatus(UnitStatus status);
}