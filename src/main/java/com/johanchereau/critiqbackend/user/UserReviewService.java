package com.johanchereau.critiqbackend.user;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.johanchereau.critiqbackend.handler.ErrorCodes.REVIEW_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final MovieRepository movieRepository;
    private final UserReviewMapper userReviewMapper;

    public PageResponse<UserReviewResponse> findReviewsByMovie(Long movieId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserReview> reviews = userReviewRepository.findByMovieId(movieId, pageable);

        List<UserReviewResponse> responses = reviews.stream()
                .map(userReviewMapper::toResponse)
                .toList();

        return new PageResponse<>(
                responses,
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isFirst(),
                reviews.isLast()
        );
    }

    public PageResponse<UserReviewResponse> findReviewsByUser(Long userId, int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(userId)) {
            throw new SecurityException("You can only view your own reviews.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserReview> reviews = userReviewRepository.findByUserId(user.getId(), pageable);

        List<UserReviewResponse> responses = reviews.stream()
                .map(userReviewMapper::toResponse)
                .toList();

        return new PageResponse<>(
                responses,
                reviews.getNumber(),
                reviews.getSize(),
                reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isFirst(),
                reviews.isLast()
        );
    }

    public UserReviewResponse findReviewByIdForMovie(Long movieId, Long reviewId) {
        UserReview review = userReviewRepository.findById(reviewId)
                .filter(r -> r.getMovie().getId().equals(movieId))
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found for movie " + movieId));

        return userReviewMapper.toResponse(review);
    }

    public UserReviewResponse findReviewById(Long reviewId) {
        return userReviewRepository.findById(reviewId)
                .map(userReviewMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found"));
    }

    public UserReviewResponse saveReview(Long movieId, UserReviewRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));

        userReviewRepository.findByUserIdAndMovieId(user.getId(), movieId).ifPresent(existing -> {
            throw new IllegalStateException(REVIEW_ALREADY_EXISTS.getMessage());
        });

        UserReview review = userReviewMapper.toUserReview(request, user, movie);
        return userReviewMapper.toResponse(userReviewRepository.save(review));
    }

    public void deleteReview(Long movieId, Long reviewId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        UserReview review = userReviewRepository.findById(reviewId)
                .filter(r -> r.getUser().getId().equals(user.getId()) && r.getMovie().getId().equals(movieId))
                .orElseThrow(() -> new EntityNotFoundException("Review not found for this user and movie"));

        userReviewRepository.delete(review);
    }
}
