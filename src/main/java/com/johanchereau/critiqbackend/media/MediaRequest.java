package com.johanchereau.critiqbackend.media;

public record MediaRequest(
        String title,
        String alt,
        String url,
        Long mediaTypeId,
        Long movieId
) {}
