package com.andesstay.report.Controller;

import com.andesstay.report.DTO.KpiResponseDTO;
import com.andesstay.report.DTO.TopUnitResponseDTO;
import com.andesstay.report.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/kpis")
    public ResponseEntity<KpiResponseDTO> getKpis(@RequestParam(defaultValue = "last24h") String range) {
        return ResponseEntity.ok(reportService.getKpis(range));
    }

    @GetMapping("/top-units")
    public ResponseEntity<List<TopUnitResponseDTO>> getTopUnits(@RequestParam(defaultValue = "last7d") String range) {
        return ResponseEntity.ok(reportService.getTopUnits(range));
    }
}