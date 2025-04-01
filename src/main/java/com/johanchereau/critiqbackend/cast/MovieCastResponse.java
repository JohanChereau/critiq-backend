package com.johanchereau.critiqbackend.cast;

public record MovieCastResponse(
        long id,
        String characterName,
        String movieTitle,
        String movieRoleName,
        String personName
) {}
