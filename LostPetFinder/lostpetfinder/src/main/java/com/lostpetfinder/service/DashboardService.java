package com.lostpetfinder.service;

import com.lostpetfinder.dto.DashboardResponse;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.repository.FoundAnimalReportRepository;
import com.lostpetfinder.repository.LostPetReportRepository;
import com.lostpetfinder.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepo;
    private final LostPetReportRepository lostRepo;
    private final FoundAnimalReportRepository foundRepo;

    public DashboardService(UserRepository userRepo, LostPetReportRepository lostRepo, FoundAnimalReportRepository foundRepo) {
        this.userRepo = userRepo;
        this.lostRepo = lostRepo;
        this.foundRepo = foundRepo;
    }

    public DashboardResponse summary() {
        return new DashboardResponse(userRepo.count(),
                lostRepo.count(), lostRepo.countByStatus(ReportStatus.ACTIVE), lostRepo.countByStatus(ReportStatus.RESOLVED),
                foundRepo.count(), foundRepo.countByStatus(ReportStatus.ACTIVE), foundRepo.countByStatus(ReportStatus.RESOLVED));
    }
}
