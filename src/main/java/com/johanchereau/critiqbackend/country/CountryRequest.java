package com.johanchereau.critiqbackend.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryRequest(
        Long id,
        @NotBlank(message = "Country name is required")
        String name,
        @NotBlank(message = "ISO Code is required")
        @Size(min = 2, max = 3, message = "ISO Code must be 2 or 3 characters long")
        String isoCode
) {}
