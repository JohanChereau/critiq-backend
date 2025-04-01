package com.johanchereau.critiqbackend.cast;

public record MovieCastRequest(
        long movieId,
        long movieRoleId,
        long personId,
        String characterName
) {}
