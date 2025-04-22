package com.johanchereau.critiqbackend.user;

import com.johanchereau.critiqbackend.movie.Movie;
import org.springframework.stereotype.Service;

@Service
public class UserReviewMapper {

    public UserReview toUserReview(UserReviewRequest request, User user, Movie movie) {
        return UserReview.builder()
                .title(request.title())
                .body(request.body())
                .rating(request.rating())
                .user(user)
                .movie(movie)
                .build();
    }

    public UserReviewResponse toResponse(UserReview review) {
        return new UserReviewResponse(
                review.getId(),
                review.getTitle(),
                review.getBody(),
                review.getRating(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getMovie().getId(),
                review.getMovie().getTitle()
        );
    }
}
