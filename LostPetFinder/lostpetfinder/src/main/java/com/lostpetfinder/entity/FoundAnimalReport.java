package com.lostpetfinder.entity;

import com.lostpetfinder.enums.ReportStatus;
import com.lostpetfinder.enums.Species;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "found_animal_reports",
       indexes = @Index(name = "idx_found_match", columnList = "species, locality, status"))
@Getter @Setter @NoArgsConstructor
public class FoundAnimalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Species species;

    @Column(length = 80)
    private String breed;

    @Column(nullable = false, length = 50)
    private String colour;

    @Column(nullable = false, length = 100)
    private String locality;

    @Column(name = "found_location", nullable = false, length = 200)
    private String foundLocation;

    @Column(name = "found_date", nullable = false)
    private LocalDate foundDate;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}
