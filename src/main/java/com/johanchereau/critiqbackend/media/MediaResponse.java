package com.johanchereau.critiqbackend.media;

public record MediaResponse(
        Long id,
        String title,
        String alt,
        String url,
        Long mediaTypeId,
        Long movieId
) {}
