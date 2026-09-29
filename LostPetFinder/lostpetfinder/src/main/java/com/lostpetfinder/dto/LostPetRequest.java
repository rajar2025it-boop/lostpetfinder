package com.lostpetfinder.dto;

import com.lostpetfinder.enums.Species;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record LostPetRequest(
        @NotNull(message = "userId is required") @Positive(message = "userId must be positive") Long userId,
        String petName,
        @NotNull(message = "species is required (DOG, CAT, BIRD, RABBIT, OTHER)") Species species,
        String breed,
        @NotBlank(message = "colour is required") String colour,
        @NotBlank(message = "locality is required") String locality,
        @NotBlank(message = "lastSeenLocation is required") String lastSeenLocation,
        @NotNull(message = "lastSeenDate is required") @PastOrPresent(message = "lastSeenDate cannot be in the future") LocalDate lastSeenDate,
        @Size(max = 500, message = "description can be at most 500 characters") String description) {}
