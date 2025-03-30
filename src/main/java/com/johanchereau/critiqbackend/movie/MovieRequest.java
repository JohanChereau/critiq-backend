package com.johanchereau.critiqbackend.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MovieRequest(
        Long id,

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        String description,
        String storyline,

        String tagline,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate,

        @Positive(message = "Budget should be positive")
        int budgetEur,

        @Size(max = 50, message = "Classification cannot exceed 50 characters")
        String classification,

        @Positive(message = "Duration must be a positive number")
        int durationMinutes,

        String posterUrl,
        String bannerUrl

//        List<MovieLanguageRequest> movieLanguages,  // Liste des langues
//        List<MovieCastRequest> movieCasts,          // Liste des membres du casting
//        List<MediaRequest> media                    // Liste des médias
) {
}
