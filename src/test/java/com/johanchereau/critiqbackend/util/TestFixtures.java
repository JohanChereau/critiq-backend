package com.johanchereau.critiqbackend.util;

import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.user.User;
import com.johanchereau.critiqbackend.user.UserReview;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;

public final class TestFixtures {
    private TestFixtures() { /* static only */ }

    public static User user(Long id, String email) {
        return User.builder().id(id).email(email).build();
    }

    public static Movie movie(Long id) {
        return Movie.builder().id(id).build();
    }

    public static UserReview review(Long id, String title) {
        return UserReview.builder().id(id).title(title).build();
    }

    public static UserReview reviewWithMovie(Long id, Movie movie) {
        return UserReview.builder().id(id).movie(movie).build();
    }

    public static UserReview reviewWithUserAndMovie(Long id, User user, Movie movie) {
        return UserReview.builder().id(id).user(user).movie(movie).build();
    }

    @SafeVarargs
    public static PageImpl<UserReview> reviewPage(UserReview... reviews) {
        return new PageImpl<>(List.of(reviews));
    }

    public static Sort sortByCreatedAtDesc() {
        return Sort.by("createdAt").descending();
    }
}
