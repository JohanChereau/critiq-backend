package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.country.Country;
import com.johanchereau.critiqbackend.genre.Genre;
import com.johanchereau.critiqbackend.language.MovieLanguage;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private String tagline;
    private String storyline;
    private LocalDate releaseDate;
    private int budgetEur;
    private String classification;
    private int durationMinutes;
    private String posterUrl;
    private String bannerUrl;
    private Set<Genre> genres;
    private Set<Country> countries;
    private List<MovieLanguage> movieLanguages;

    private int totalReviews;
    private double averageRating;
}
