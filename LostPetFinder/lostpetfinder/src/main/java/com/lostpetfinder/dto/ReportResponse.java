package com.lostpetfinder.dto;

import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** One response shape for both LOST and FOUND reports. */
public record ReportResponse(
        Long id, String type,
        Long reporterId, String reporterName, String reporterPhone,
        String petName, Species species, String breed, String colour,
        String locality, String location, LocalDate date, String description,
        ReportStatus status, LocalDateTime createdAt, LocalDateTime resolvedAt) {}
