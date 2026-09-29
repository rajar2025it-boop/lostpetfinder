package com.lostpetfinder.service;

import com.lostpetfinder.dto.MatchResponse;
import com.lostpetfinder.entity.FoundAnimalReport;
import com.lostpetfinder.entity.LostPetReport;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.exception.BusinessRuleException;
import com.lostpetfinder.mapper.ReportMapper;
import com.lostpetfinder.repository.FoundAnimalReportRepository;
import com.lostpetfinder.repository.LostPetReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Feature 3: suggest matches.
 * Hard rules  : species AND locality must match (case-insensitive), candidate must be ACTIVE.
 * Ranking bonus: colour (+2), breed (+1). Base score = 3 (species + locality).
 */
@Service
public class MatchService {

    private final LostPetReportService lostService;
    private final FoundAnimalReportService foundService;
    private final LostPetReportRepository lostRepo;
    private final FoundAnimalReportRepository foundRepo;

    public MatchService(LostPetReportService lostService, FoundAnimalReportService foundService,
                        LostPetReportRepository lostRepo, FoundAnimalReportRepository foundRepo) {
        this.lostService = lostService;
        this.foundService = foundService;
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
    }

    /** Given a LOST report, find ACTIVE FOUND reports. */
    @Transactional(readOnly = true)
    public List<MatchResponse> suggestForLost(Long lostId) {
        LostPetReport lost = lostService.find(lostId);
        // Business rule: checked in the service layer, BEFORE any query runs
        if (lost.getStatus() == ReportStatus.RESOLVED) {
            throw new BusinessRuleException("Lost report " + lostId + " is resolved; resolved reports get no match suggestions");
        }
        List<MatchResponse> out = new ArrayList<>();
        for (FoundAnimalReport f : foundRepo.findBySpeciesAndLocalityIgnoreCaseAndStatus(
                lost.getSpecies(), lost.getLocality(), ReportStatus.ACTIVE)) {
            out.add(score(lost.getColour(), lost.getBreed(), f.getColour(), f.getBreed(), ReportMapper.toResponse(f)));
        }
        return sorted(out);
    }

    /** Given a FOUND report, find ACTIVE LOST reports. */
    @Transactional(readOnly = true)
    public List<MatchResponse> suggestForFound(Long foundId) {
        FoundAnimalReport found = foundService.find(foundId);
        if (found.getStatus() == ReportStatus.RESOLVED) {
            throw new BusinessRuleException("Found report " + foundId + " is resolved; resolved reports get no match suggestions");
        }
        List<MatchResponse> out = new ArrayList<>();
        for (LostPetReport l : lostRepo.findBySpeciesAndLocalityIgnoreCaseAndStatus(
                found.getSpecies(), found.getLocality(), ReportStatus.ACTIVE)) {
            out.add(score(found.getColour(), found.getBreed(), l.getColour(), l.getBreed(), ReportMapper.toResponse(l)));
        }
        return sorted(out);
    }

    private MatchResponse score(String colourA, String breedA, String colourB, String breedB,
                                com.lostpetfinder.dto.ReportResponse candidate) {
        int score = 3;
        List<String> matched = new ArrayList<>(List.of("species", "locality"));
        if (colourA != null && colourA.equalsIgnoreCase(colourB)) { score += 2; matched.add("colour"); }
        if (breedA != null && breedA.equalsIgnoreCase(breedB))    { score += 1; matched.add("breed"); }
        return new MatchResponse(candidate, score, matched);
    }

    private List<MatchResponse> sorted(List<MatchResponse> list) {
        list.sort(Comparator.comparingInt(MatchResponse::score).reversed());
        return list;
    }
}
