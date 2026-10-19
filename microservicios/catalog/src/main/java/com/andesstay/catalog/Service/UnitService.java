package com.andesstay.catalog.Service;

import com.andesstay.catalog.DTO.UnitCreateRequest;
import com.andesstay.catalog.DTO.UnitResponse;
import com.andesstay.catalog.Model.Unit;
import com.andesstay.catalog.Model.UnitStatus;
import com.andesstay.catalog.Repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<UnitResponse> getAllUnits(UnitStatus status) {
        List<Unit> units;
        if (status != null) {
            units = unitRepository.findByStatus(status);
        } else {
            units = unitRepository.findAll();
        }
        return units.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public UnitResponse getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada con ID: " + id));
        return mapToResponse(unit);
    }

    @Transactional
    public UnitResponse createUnit(UnitCreateRequest request) {
        Unit unit = new Unit();
        unit.setName(request.getName());
        unit.setType(request.getType());
        unit.setCapacity(request.getCapacity());
        unit.setPricePerNight(request.getPricePerNight());
        if (request.getStatus() != null) {
            unit.setStatus(request.getStatus());
        }

        Unit saved = unitRepository.save(unit);
        return mapToResponse(saved);
    }

    @Transactional
    public UnitResponse updateUnit(Long id, UnitCreateRequest request) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada con ID: " + id));

        if (request.getName() != null)
            unit.setName(request.getName());
        if (request.getType() != null)
            unit.setType(request.getType());
        if (request.getCapacity() != null)
            unit.setCapacity(request.getCapacity());
        if (request.getPricePerNight() != null)
            unit.setPricePerNight(request.getPricePerNight());
        if (request.getStatus() != null)
            unit.setStatus(request.getStatus());

        Unit updated = unitRepository.save(unit);
        return mapToResponse(updated);
    }

    @Transactional
    public UnitResponse updateAvailability(Long id, UnitStatus status) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad no encontrada con ID: " + id));
        unit.setStatus(status);
        Unit updated = unitRepository.save(unit);
        return mapToResponse(updated);
    }

    private UnitResponse mapToResponse(Unit unit) {
        UnitResponse response = new UnitResponse();
        response.setId(unit.getId());
        response.setName(unit.getName());
        response.setType(unit.getType());
        response.setCapacity(unit.getCapacity());
        response.setPricePerNight(unit.getPricePerNight());
        response.setStatus(unit.getStatus());
        response.setCreatedAt(unit.getCreatedAt());
        return response;
    }
}