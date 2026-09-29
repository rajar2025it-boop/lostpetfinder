package com.lostpetfinder.repository;

import com.lostpetfinder.entity.LostPetReport;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LostPetReportRepository extends JpaRepository<LostPetReport, Long> {

    // used by match suggestions: species + locality, ACTIVE only
    List<LostPetReport> findBySpeciesAndLocalityIgnoreCaseAndStatus(Species species, String locality, ReportStatus status);

    // feature 5: search by locality (+ optional filters)
    @Query("select r from LostPetReport r where lower(r.locality) like lower(concat('%', :locality, '%')) " +
           "and (:species is null or r.species = :species) and (:status is null or r.status = :status) " +
           "order by r.createdAt desc")
    List<LostPetReport> search(@Param("locality") String locality,
                               @Param("species") Species species,
                               @Param("status") ReportStatus status);

    long countByStatus(ReportStatus status);
}
