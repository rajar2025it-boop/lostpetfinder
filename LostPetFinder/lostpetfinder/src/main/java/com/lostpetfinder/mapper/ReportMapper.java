package com.lostpetfinder.mapper;

import com.lostpetfinder.dto.ReportResponse;
import com.lostpetfinder.dto.UserResponse;
import com.lostpetfinder.entity.FoundAnimalReport;
import com.lostpetfinder.entity.LostPetReport;
import com.lostpetfinder.entity.User;

public final class ReportMapper {
    private ReportMapper() {}

    public static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone());
    }

    public static ReportResponse toResponse(LostPetReport r) {
        User u = r.getUser();
        return new ReportResponse(r.getId(), "LOST", u.getId(), u.getName(), u.getPhone(),
                r.getPetName(), r.getSpecies(), r.getBreed(), r.getColour(),
                r.getLocality(), r.getLastSeenLocation(), r.getLastSeenDate(), r.getDescription(),
                r.getStatus(), r.getCreatedAt(), r.getResolvedAt());
    }

    public static ReportResponse toResponse(FoundAnimalReport r) {
        User u = r.getUser();
        return new ReportResponse(r.getId(), "FOUND", u.getId(), u.getName(), u.getPhone(),
                null, r.getSpecies(), r.getBreed(), r.getColour(),
                r.getLocality(), r.getFoundLocation(), r.getFoundDate(), r.getDescription(),
                r.getStatus(), r.getCreatedAt(), r.getResolvedAt());
    }
}
