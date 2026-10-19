package com.andesstay.catalog.Controller;

import com.andesstay.catalog.DTO.UnitAvailabilityUpdateRequest;
import com.andesstay.catalog.DTO.UnitCreateRequest;
import com.andesstay.catalog.DTO.UnitResponse;
import com.andesstay.catalog.Model.UnitStatus;
import com.andesstay.catalog.Service.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits(
            @RequestParam(required = false) UnitStatus status) {
        List<UnitResponse> response = unitService.getAllUnits(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable Long id) {
        UnitResponse response = unitService.getUnitById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@RequestBody UnitCreateRequest request) {
        UnitResponse response = unitService.createUnit(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(
            @PathVariable Long id,
            @RequestBody UnitCreateRequest request) {
        UnitResponse response = unitService.updateUnit(id, request);
        return ResponseEntity.ok(response);
    }

    @org.springframework.beans.factory.annotation.Value("${internal.api.key}")
    private String internalApiKey;

    @PatchMapping("/{id}/availability")
    public ResponseEntity<UnitResponse> updateAvailability(
            @PathVariable Long id,
            @RequestBody UnitAvailabilityUpdateRequest request,
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        if (!internalApiKey.equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UnitResponse response = unitService.updateAvailability(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}