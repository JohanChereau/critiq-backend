package com.johanchereau.critiqbackend.user;

import jakarta.validation.constraints.*;

public record UserReviewRequest(
        @NotBlank String title,
        String body,
        @NotNull @Min(1) @Max(5) Integer rating
) {}
