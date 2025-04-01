package com.johanchereau.critiqbackend.cast;

import jakarta.validation.constraints.NotBlank;

public record MovieRoleRequest(
        @NotBlank String name
) {}
