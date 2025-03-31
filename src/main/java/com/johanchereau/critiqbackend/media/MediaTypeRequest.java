package com.johanchereau.critiqbackend.media;

import jakarta.validation.constraints.NotBlank;

public record MediaTypeRequest(
        @NotBlank String type
) {}
