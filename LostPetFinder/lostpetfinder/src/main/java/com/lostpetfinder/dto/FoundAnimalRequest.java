package com.lostpetfinder.dto;

import com.lostpetfinder.enums.Species;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record FoundAnimalRequest(
        @NotNull(message = "userId is required") @Positive(message = "userId must be positive") Long userId,
        @NotNull(message = "species is required (DOG, CAT, BIRD, RABBIT, OTHER)") Species species,
        String breed,
        @NotBlank(message = "colour is required") String colour,
        @NotBlank(message = "locality is required") String locality,
        @NotBlank(message = "foundLocation is required") String foundLocation,
        @NotNull(message = "foundDate is required") @PastOrPresent(message = "foundDate cannot be in the future") LocalDate foundDate,
        @Size(max = 500, message = "description can be at most 500 characters") String description) {}
