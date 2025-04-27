package com.johanchereau.critiqbackend.util;

import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import com.johanchereau.critiqbackend.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating and persisting test data for integration tests.
 */
@Component
@RequiredArgsConstructor
public class TestDataFactory {

    // === Default Constants ===
    public static final String DEFAULT_USERNAME = "johndoe";
    public static final String DEFAULT_EMAIL = "john.doe@example.com";
    public static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

    public static final String OTHER_USERNAME = "otheruser";
    public static final String OTHER_EMAIL = "other.user@example.com";

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_EMAIL = "admin@example.com";

    public static final String DEFAULT_MOVIE_TITLE = "Interstellar";
    public static final LocalDate DEFAULT_MOVIE_RELEASE_DATE = LocalDate.of(2014, 11, 7);

    public static final String DEFAULT_REVIEW_TITLE = "Amazing movie";
    public static final String DEFAULT_REVIEW_BODY = "One of the best movies ever!";
    public static final int DEFAULT_REVIEW_RATING = 5;

    // === Dependencies ===
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final UserReviewRepository userReviewRepository;
    private final UserRoleRepository userRoleRepository;

    // === Movie Methods ===

    /**
     * Creates and saves a default movie.
     */
    public Movie createDefaultMovie() {
        return createMovie(DEFAULT_MOVIE_TITLE, DEFAULT_MOVIE_RELEASE_DATE);
    }

    /**
     * Creates and saves a movie with given title and release date.
     */
    public Movie createMovie(String title, LocalDate releaseDate) {
        return movieRepository.save(Movie.builder()
                .title(title)
                .releaseDate(releaseDate)
                .build());
    }

    // === User Methods ===

    /**
     * Creates and saves a default user.
     */
    public User createDefaultUser() {
        return createUser(DEFAULT_USERNAME, DEFAULT_EMAIL, List.of());
    }

    /**
     * Creates and saves another default user.
     */
    public User createDefaultOtherUser() {
        return createUser(OTHER_USERNAME, OTHER_EMAIL, List.of());
    }

    /**
     * Creates and saves an admin user with persisted role.
     */
    public User createDefaultAdmin() {
        UserRole adminRole = userRoleRepository.save(
                UserRole.builder().name("ROLE_ADMIN").build()
        );
        return createUser(ADMIN_USERNAME, ADMIN_EMAIL, List.of(adminRole));
    }

    /**
     * Creates and saves a user with given username, email, and roles.
     * Automatically sets all required fields to sensible defaults.
     */
    public User createUser(String username, String email, List<UserRole> roles) {
        User user = User.builder()
                .username(username)
                .email(email)
                .firstname(username)
                .lastname("Test")
                .password("password")
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .termsAccepted(true)
                .termsAcceptedAt(LocalDateTime.now())
                .newsletterAccepted(false)
                .newsletterAcceptedAt(null)
                .enabled(true)
                .accountLocked(false)
                .userRoles(roles)
                .favorites(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();
        return userRepository.save(user);
    }

    // === Review Methods ===

    /**
     * Creates and saves a default review for a user and a movie.
     */
    public UserReview createDefaultReview(User user, Movie movie) {
        return createReview(user, movie, DEFAULT_REVIEW_TITLE, DEFAULT_REVIEW_RATING);
    }

    /**
     * Creates and saves a review for a user and a movie with given title and rating.
     */
    public UserReview createReview(User user, Movie movie, String title, int rating) {
        return userReviewRepository.save(UserReview.builder()
                .user(user)
                .movie(movie)
                .title(title)
                .rating(rating)
                .build());
    }
}