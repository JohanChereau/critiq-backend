package com.johanchereau.critiqbackend.user;

public record UserReviewResponse(
        Long id,
        String title,
        String body,
        Integer rating,
        Long userId,
        String username,
        Long movieId,
        String movieTitle
) {}
