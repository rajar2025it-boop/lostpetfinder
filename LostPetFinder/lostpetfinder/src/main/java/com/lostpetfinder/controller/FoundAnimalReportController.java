package com.lostpetfinder.controller;

import com.lostpetfinder.dto.FoundAnimalRequest;
import com.lostpetfinder.dto.ReportResponse;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import com.lostpetfinder.service.FoundAnimalReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/found-reports")
public class FoundAnimalReportController {

    private final FoundAnimalReportService service;

    public FoundAnimalReportController(FoundAnimalReportService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody FoundAnimalRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/{id}")
    public ReportResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public List<ReportResponse> search(@RequestParam(required = false) String locality,
                                       @RequestParam(required = false) Species species,
                                       @RequestParam(required = false) ReportStatus status) {
        return service.search(locality, species, status);
    }

    @PutMapping("/{id}/resolve")
    public ReportResponse resolve(@PathVariable Long id) { return service.resolve(id); }
}
