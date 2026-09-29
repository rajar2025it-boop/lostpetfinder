package com.lostpetfinder.controller;

import com.lostpetfinder.dto.LostPetRequest;
import com.lostpetfinder.dto.ReportResponse;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import com.lostpetfinder.service.LostPetReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost-reports")
public class LostPetReportController {

    private final LostPetReportService service;

    public LostPetReportController(LostPetReportService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody LostPetRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/{id}")
    public ReportResponse get(@PathVariable Long id) { return service.get(id); }

    /** GET /api/lost-reports?locality=Peelamedu&species=DOG&status=ACTIVE  (all params optional) */
    @GetMapping
    public List<ReportResponse> search(@RequestParam(required = false) String locality,
                                       @RequestParam(required = false) Species species,
                                       @RequestParam(required = false) ReportStatus status) {
        return service.search(locality, species, status);
    }

    /** PUT /api/lost-reports/{id}/resolve?foundReportId=5  (foundReportId optional) */
    @PutMapping("/{id}/resolve")
    public ReportResponse resolve(@PathVariable Long id, @RequestParam(required = false) Long foundReportId) {
        return service.resolve(id, foundReportId);
    }
}
