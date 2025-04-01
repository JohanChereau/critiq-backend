package com.johanchereau.critiqbackend.cast;

import java.time.LocalDate;

public record PersonResponse(
        Long id,
        String name,
        String roles,
        String photoUrl,
        String bio,
        LocalDate dateOfBirth
) {}
