package com.lostpetfinder.service;

import com.lostpetfinder.dto.LostPetRequest;
import com.lostpetfinder.dto.ReportResponse;
import com.lostpetfinder.entity.FoundAnimalReport;
import com.lostpetfinder.entity.LostPetReport;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import com.lostpetfinder.exception.BusinessRuleException;
import com.lostpetfinder.exception.ResourceNotFoundException;
import com.lostpetfinder.mapper.ReportMapper;
import com.lostpetfinder.repository.FoundAnimalReportRepository;
import com.lostpetfinder.repository.LostPetReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LostPetReportService {

    private final LostPetReportRepository lostRepo;
    private final FoundAnimalReportRepository foundRepo;
    private final UserService userService;

    public LostPetReportService(LostPetReportRepository lostRepo, FoundAnimalReportRepository foundRepo,
                                UserService userService) {
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
        this.userService = userService;
    }

    /** Feature 1: report a lost pet. */
    @Transactional
    public ReportResponse create(LostPetRequest req) {
        LostPetReport r = new LostPetReport();
        r.setUser(userService.find(req.userId()));
        r.setPetName(req.petName());
        r.setSpecies(req.species());
        r.setBreed(req.breed());
        r.setColour(req.colour().trim());
        r.setLocality(req.locality().trim());
        r.setLastSeenLocation(req.lastSeenLocation().trim());
        r.setLastSeenDate(req.lastSeenDate());
        r.setDescription(req.description());
        r.setStatus(ReportStatus.ACTIVE);
        return ReportMapper.toResponse(lostRepo.save(r));
    }

    @Transactional(readOnly = true)
    public ReportResponse get(Long id) { return ReportMapper.toResponse(find(id)); }

    /** Feature 5: search by locality (species / status optional). */
    @Transactional(readOnly = true)
    public List<ReportResponse> search(String locality, Species species, ReportStatus status) {
        return lostRepo.search(locality == null ? "" : locality.trim(), species, status)
                .stream().map(ReportMapper::toResponse).toList();
    }

    /**
     * Feature 4: mark as resolved. If foundReportId is given, the matching found report is
     * resolved in the same transaction (both sides of the reunion are closed together).
     */
    @Transactional
    public ReportResponse resolve(Long id, Long foundReportId) {
        LostPetReport lost = find(id);
        if (lost.getStatus() == ReportStatus.RESOLVED) {
            throw new BusinessRuleException("Lost report " + id + " is already resolved");
        }
        if (foundReportId != null) {
            FoundAnimalReport found = foundRepo.findById(foundReportId)
                    .orElseThrow(() -> new ResourceNotFoundException("Found report not found with id " + foundReportId));
            if (found.getStatus() == ReportStatus.RESOLVED) {
                throw new BusinessRuleException("Found report " + foundReportId + " is already resolved");
            }
            if (found.getSpecies() != lost.getSpecies()) {
                throw new BusinessRuleException("Cannot link reports with different species");
            }
            found.setStatus(ReportStatus.RESOLVED);
            found.setResolvedAt(LocalDateTime.now());
        }
        lost.setStatus(ReportStatus.RESOLVED);
        lost.setResolvedAt(LocalDateTime.now());
        return ReportMapper.toResponse(lost);
    }

    public LostPetReport find(Long id) {
        return lostRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost report not found with id " + id));
    }
}
