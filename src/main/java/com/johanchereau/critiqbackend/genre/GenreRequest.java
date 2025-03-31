package com.johanchereau.critiqbackend.genre;

import jakarta.validation.constraints.NotBlank;

public record GenreRequest(
        @NotBlank String name
) {}
