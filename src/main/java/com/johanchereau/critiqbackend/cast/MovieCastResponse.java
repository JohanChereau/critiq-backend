package com.johanchereau.critiqbackend.cast;

public record MovieCastResponse(
        long id,
        String characterName,
        long movieId,
        String movieTitle,
        long movieRoleId,
        String movieRoleName,
        long personId,
        String personName
) {}
