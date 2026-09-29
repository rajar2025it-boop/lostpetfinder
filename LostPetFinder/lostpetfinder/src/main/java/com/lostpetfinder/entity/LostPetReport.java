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
@Table(name = "lost_pet_reports",
       indexes = @Index(name = "idx_lost_match", columnList = "species, locality, status"))
@Getter @Setter @NoArgsConstructor
public class LostPetReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "pet_name", length = 80)
    private String petName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Species species;

    @Column(length = 80)
    private String breed;

    @Column(nullable = false, length = 50)
    private String colour;

    @Column(nullable = false, length = 100)
    private String locality;

    @Column(name = "last_seen_location", nullable = false, length = 200)
    private String lastSeenLocation;

    @Column(name = "last_seen_date", nullable = false)
    private LocalDate lastSeenDate;

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
