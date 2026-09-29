package com.lostpetfinder.service;

import com.lostpetfinder.dto.FoundAnimalRequest;
import com.lostpetfinder.dto.ReportResponse;
import com.lostpetfinder.entity.FoundAnimalReport;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import com.lostpetfinder.exception.BusinessRuleException;
import com.lostpetfinder.exception.ResourceNotFoundException;
import com.lostpetfinder.mapper.ReportMapper;
import com.lostpetfinder.repository.FoundAnimalReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoundAnimalReportService {

    private final FoundAnimalReportRepository foundRepo;
    private final UserService userService;

    public FoundAnimalReportService(FoundAnimalReportRepository foundRepo, UserService userService) {
        this.foundRepo = foundRepo;
        this.userService = userService;
    }

    /** Feature 2: report a found animal. */
    @Transactional
    public ReportResponse create(FoundAnimalRequest req) {
        FoundAnimalReport r = new FoundAnimalReport();
        r.setUser(userService.find(req.userId()));
        r.setSpecies(req.species());
        r.setBreed(req.breed());
        r.setColour(req.colour().trim());
        r.setLocality(req.locality().trim());
        r.setFoundLocation(req.foundLocation().trim());
        r.setFoundDate(req.foundDate());
        r.setDescription(req.description());
        r.setStatus(ReportStatus.ACTIVE);
        return ReportMapper.toResponse(foundRepo.save(r));
    }

    @Transactional(readOnly = true)
    public ReportResponse get(Long id) { return ReportMapper.toResponse(find(id)); }

    @Transactional(readOnly = true)
    public List<ReportResponse> search(String locality, Species species, ReportStatus status) {
        return foundRepo.search(locality == null ? "" : locality.trim(), species, status)
                .stream().map(ReportMapper::toResponse).toList();
    }

    /** Feature 4 (found side): mark resolved. */
    @Transactional
    public ReportResponse resolve(Long id) {
        FoundAnimalReport found = find(id);
        if (found.getStatus() == ReportStatus.RESOLVED) {
            throw new BusinessRuleException("Found report " + id + " is already resolved");
        }
        found.setStatus(ReportStatus.RESOLVED);
        found.setResolvedAt(LocalDateTime.now());
        return ReportMapper.toResponse(found);
    }

    public FoundAnimalReport find(Long id) {
        return foundRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found report not found with id " + id));
    }
}
