package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.user.UserReview;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieMapper {

    public Movie toMovie(MovieRequest movieRequest) {
        return Movie.builder()
                .id(movieRequest.id())
                .title(movieRequest.title())
                .description(movieRequest.description())
                .storyline(movieRequest.storyline())
                .tagline(movieRequest.tagline())
                .releaseDate(movieRequest.releaseDate())
                .budgetEur(movieRequest.budgetEur())
                .classification(movieRequest.classification())
                .durationMinutes(movieRequest.durationMinutes())
                .posterUrl(movieRequest.posterUrl())
                .bannerUrl(movieRequest.bannerUrl())
                .build();
    }

    public MovieResponse toMovieResponse(Movie movie) {
        Double averageRating = movie.getReviews().isEmpty()
                ? null
                : movie.getReviews().stream()
                .mapToInt(UserReview::getRating)
                .average()
                .orElse(0.0);

        int totalReviews = movie.getReviews().size();

        movie.setAverageRating(averageRating);
        movie.setTotalReviews(totalReviews);

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .tagline(movie.getTagline())
                .storyline(movie.getStoryline())
                .releaseDate(movie.getReleaseDate())
                .budgetEur(movie.getBudgetEur())
                .classification(movie.getClassification())
                .durationMinutes(movie.getDurationMinutes())
                .posterUrl(movie.getPosterUrl())
                .bannerUrl(movie.getBannerUrl())
                .genres(movie.getGenres())
                .countries(movie.getCountries())
                .movieLanguages(movie.getMovieLanguages())
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .build();
    }
}
