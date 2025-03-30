package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.cast.MovieCast;
import com.johanchereau.critiqbackend.country.Country;
import com.johanchereau.critiqbackend.favorite.Favorite;
import com.johanchereau.critiqbackend.genre.Genre;
import com.johanchereau.critiqbackend.language.MovieLanguage;
import com.johanchereau.critiqbackend.media.Media;
import com.johanchereau.critiqbackend.user.UserReview;
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
    private List<Media> media;
    private List<MovieLanguage> movieLanguages;
    private List<MovieCast> casts;
    private List<UserReview> reviews;
    private List<Favorite> favorites;
}
