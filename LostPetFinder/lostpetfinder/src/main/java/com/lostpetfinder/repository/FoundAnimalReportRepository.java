package com.lostpetfinder.repository;

import com.lostpetfinder.entity.FoundAnimalReport;
import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoundAnimalReportRepository extends JpaRepository<FoundAnimalReport, Long> {

    List<FoundAnimalReport> findBySpeciesAndLocalityIgnoreCaseAndStatus(Species species, String locality, ReportStatus status);

    @Query("select r from FoundAnimalReport r where lower(r.locality) like lower(concat('%', :locality, '%')) " +
           "and (:species is null or r.species = :species) and (:status is null or r.status = :status) " +
           "order by r.createdAt desc")
    List<FoundAnimalReport> search(@Param("locality") String locality,
                                   @Param("species") Species species,
                                   @Param("status") ReportStatus status);

    long countByStatus(ReportStatus status);
}
