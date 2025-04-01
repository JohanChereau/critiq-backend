package com.johanchereau.critiqbackend.movie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.johanchereau.critiqbackend.cast.MovieCast;
import com.johanchereau.critiqbackend.common.BaseEntity;
import com.johanchereau.critiqbackend.country.Country;
import com.johanchereau.critiqbackend.favorite.Favorite;
import com.johanchereau.critiqbackend.genre.Genre;
import com.johanchereau.critiqbackend.language.MovieLanguage;
import com.johanchereau.critiqbackend.media.Media;
import com.johanchereau.critiqbackend.user.UserReview;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Movie extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String description;
    private String tagline;
    private String storyline;

    @Column(nullable = false)
    private LocalDate releaseDate;

    private int budgetEur;
    private String classification;

    @Column(nullable = false)
    private int durationMinutes;

    private String posterUrl;
    private String bannerUrl;

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "movie_country",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private Set<Country> countries;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> media;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieLanguage> movieLanguages;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCast> casts;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserReview> reviews;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;
}
