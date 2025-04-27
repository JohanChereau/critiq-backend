package com.johanchereau.critiqbackend.user;

import com.johanchereau.critiqbackend.movie.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.johanchereau.critiqbackend.util.TestFixtures.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Tag("Unit")
@DisplayName("Unit Tests - UserReviewService")
@ExtendWith(MockitoExtension.class)
class UserReviewServiceTest {

    private static final String DEFAULT_EMAIL = "test@example.com";

    @Mock
    private UserReviewRepository userReviewRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserReviewMapper userReviewMapper;
    @InjectMocks
    private UserReviewService userReviewService;

    private Authentication authFor(String email) {
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user(1L, email)));
        return auth;
    }

    @Nested
    @DisplayName("Find Reviews")
    class FindReviews {

        @Test
        @DisplayName("by movie id")
        void byMovieId() {
            long movieId = 1L;
            var reviews = reviewPage(
                    review(1L, "Review A"),
                    review(2L, "Review B")
            );
            given(userReviewRepository.findByMovieId(movieId, PageRequest.of(0, 10, sortByCreatedAtDesc())))
                    .willReturn(reviews);
            given(userReviewMapper.toResponse(review(1L, "Review A")))
                    .willReturn(new UserReviewResponse(1L, "Review A", null, null, null, null, null, null));
            given(userReviewMapper.toResponse(review(2L, "Review B")))
                    .willReturn(new UserReviewResponse(2L, "Review B", null, null, null, null, null, null));

            var result = userReviewService.findReviewsByMovie(movieId, 0, 10);
            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("empty when no reviews")
        void emptyWhenNoReviews() {
            long movieId = 99L;
            given(userReviewRepository.findByMovieId(movieId, PageRequest.of(0, 10, sortByCreatedAtDesc())))
                    .willReturn(new PageImpl<>(emptyList()));

            var result = userReviewService.findReviewsByMovie(movieId, 0, 10);
            assertEquals(0, result.getContent().size());
        }

        @Test
        @DisplayName("by connected user")
        void byConnectedUser() {
            var auth = authFor(DEFAULT_EMAIL);
            long userId = 1L;
            var reviews = reviewPage(
                    review(1L, "R1"),
                    review(2L, "R2")
            );
            given(userReviewRepository.findByUserId(userId, PageRequest.of(0, 10, sortByCreatedAtDesc())))
                    .willReturn(reviews);
            given(userReviewMapper.toResponse(review(1L, "R1")))
                    .willReturn(new UserReviewResponse(1L, "R1", null, null, userId, "user", null, null));
            given(userReviewMapper.toResponse(review(2L, "R2")))
                    .willReturn(new UserReviewResponse(2L, "R2", null, null, userId, "user", null, null));

            var result = userReviewService.findReviewsByUser(userId, 0, 10, auth);
            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("for correct movie")
        void forCorrectMovie() {
            long movieId = 1L;
            long reviewId = 10L;
            var movie = movie(movieId);
            var review = reviewWithMovie(reviewId, movie);
            given(userReviewRepository.findById(reviewId)).willReturn(Optional.of(review));
            given(userReviewMapper.toResponse(review))
                    .willReturn(new UserReviewResponse(reviewId, "Match", null, null, null, null, movieId, null));

            var result = userReviewService.findReviewByIdForMovie(movieId, reviewId);
            assertEquals("Match", result.title());
        }

        @Test
        @DisplayName("when exists by id")
        void whenExistsById() {
            long reviewId = 1L;
            var review = review(reviewId, "Test");
            given(userReviewRepository.findById(reviewId)).willReturn(Optional.of(review));
            given(userReviewMapper.toResponse(review))
                    .willReturn(new UserReviewResponse(reviewId, "Test", null, null, null, null, null, null));

            var result = userReviewService.findReviewById(reviewId);
            assertEquals("Test", result.title());
        }
    }

    @Nested
    @DisplayName("Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("deny access to others' reviews")
        void denyAccessOthersReviews() {
            var auth = authFor(DEFAULT_EMAIL);
            assertThrows(SecurityException.class,
                    () -> userReviewService.findReviewsByUser(999L, 0, 10, auth)
            );
        }

        @Test
        @DisplayName("review mismatch movie")
        void reviewMismatchMovie() {
            long movieId = 1L, reviewId = 10L;
            var otherMovie = movie(2L);
            var review = reviewWithMovie(reviewId, otherMovie);
            given(userReviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            assertThrows(EntityNotFoundException.class,
                    () -> userReviewService.findReviewByIdForMovie(movieId, reviewId)
            );
        }

        @Test
        @DisplayName("not found by id")
        void notFoundById() {
            given(userReviewRepository.findById(99L)).willReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class,
                    () -> userReviewService.findReviewById(99L)
            );
        }

        @Test
        @DisplayName("duplicate review")
        void duplicateReview() {
            var auth = authFor(DEFAULT_EMAIL);
            long userId = 1L, movieId = 2L;
            given(movieRepository.findById(movieId)).willReturn(Optional.of(movie(movieId)));
            given(userReviewRepository.findByUserIdAndMovieId(userId, movieId))
                    .willReturn(Optional.of(review(0L, "")));

            assertThrows(IllegalStateException.class,
                    () -> userReviewService.saveReview(movieId, new UserReviewRequest("X", "Y", 3), auth)
            );
        }

        @Test
        @DisplayName("delete mismatch or not owner")
        void deleteMismatchOrNotOwner() {
            var auth = authFor(DEFAULT_EMAIL);
            long reviewId = 10L, wrongMovie = 999L;
            var review = reviewWithUserAndMovie(reviewId, user(2L, "other@example.com"), movie(1L));
            given(userReviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            assertThrows(EntityNotFoundException.class,
                    () -> userReviewService.deleteReview(wrongMovie, reviewId, auth)
            );
        }
    }

    @Nested
    @DisplayName("Save & Delete")
    class SaveDelete {

        @Test
        @DisplayName("save success")
        void saveSuccess() {
            var auth = authFor(DEFAULT_EMAIL);
            long userId = 1L, movieId = 2L;
            var mv = movie(movieId);
            var reviewEntity = review(1L, "Great");

            given(movieRepository.findById(movieId)).willReturn(Optional.of(mv));
            given(userReviewRepository.findByUserIdAndMovieId(userId, movieId)).willReturn(Optional.empty());
            given(userReviewMapper.toUserReview(any(), any(), any())).willReturn(reviewEntity);
            given(userReviewRepository.save(reviewEntity)).willReturn(reviewEntity);
            given(userReviewMapper.toResponse(reviewEntity))
                    .willReturn(new UserReviewResponse(1L, "Great", null, null, userId, "u", movieId, "M"));

            var result = userReviewService.saveReview(movieId, new UserReviewRequest("Great", "", 5), auth);
            assertEquals("Great", result.title());
        }

        @Test
        @DisplayName("delete success")
        void deleteSuccess() {
            var auth = authFor(DEFAULT_EMAIL);
            long movieId = 2L, reviewId = 10L;
            var review = reviewWithUserAndMovie(reviewId, user(1L, DEFAULT_EMAIL), movie(movieId));
            given(userReviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            assertDoesNotThrow(() -> userReviewService.deleteReview(movieId, reviewId, auth));
            verify(userReviewRepository).delete(review);
        }
    }
}