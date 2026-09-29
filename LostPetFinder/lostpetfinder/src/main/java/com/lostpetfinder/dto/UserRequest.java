package com.lostpetfinder.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "email is required") @Email(message = "email format is invalid") String email,
        @NotBlank(message = "phone is required")
        @Pattern(regexp = "\\d{10}", message = "phone must be exactly 10 digits") String phone) {}
