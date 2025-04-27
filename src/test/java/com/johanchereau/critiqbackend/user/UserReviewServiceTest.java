package com.johanchereau.critiqbackend.user;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserReviewService}.
 */
@Tag("Unit")
@DisplayName("Unit Tests - UserReviewService")
class UserReviewServiceTest {

    private static final String DEFAULT_EMAIL = "test@example.com";
    private AutoCloseable mocks;
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

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    /**
     * Helper to mock an Authentication object returning a given email.
     */
    private Authentication mockAuthenticationWithEmail(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        when(auth.getPrincipal()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(
                Optional.of(User.builder().id(1L).email(email).build())
        );
        return auth;
    }

    // === FIND OPERATIONS ===

    @Nested
    @DisplayName("Find Reviews")
    class FindReviews {

        @Test
        @DisplayName("Should return reviews for a given movie")
        void shouldReturnReviewsByMovieId() {
            Long movieId = 1L;
            UserReview review1 = UserReview.builder().id(1L).title("Review A").build();
            UserReview review2 = UserReview.builder().id(2L).title("Review B").build();
            Page<UserReview> page = new PageImpl<>(List.of(review1, review2));
            when(userReviewRepository.findByMovieId(eq(movieId), any())).thenReturn(page);
            when(userReviewMapper.toResponse(any())).thenAnswer(invocation ->
                    new UserReviewResponse(invocation.<UserReview>getArgument(0).getId(), invocation.<UserReview>getArgument(0).getTitle(), null, null, null, null, null, null)
            );

            PageResponse<UserReviewResponse> result = userReviewService.findReviewsByMovie(movieId, 0, 10);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("Should return empty page when no reviews for movie")
        void shouldReturnEmptyPageWhenNoReviewsForMovie() {
            when(userReviewRepository.findByMovieId(eq(99L), any())).thenReturn(Page.empty());

            PageResponse<UserReviewResponse> result = userReviewService.findReviewsByMovie(99L, 0, 10);

            assertTrue(result.getContent().isEmpty());
        }

        @Test
        @DisplayName("Should return reviews for connected user")
        void shouldReturnUserReviewsWhenUserIsOwner() {
            Authentication auth = mockAuthenticationWithEmail(DEFAULT_EMAIL);
            Long userId = 1L;

            List<UserReview> reviews = List.of(
                    UserReview.builder().id(1L).title("Review 1").build(),
                    UserReview.builder().id(2L).title("Review 2").build()
            );
            when(userReviewRepository.findByUserId(eq(userId), any())).thenReturn(new PageImpl<>(reviews));
            when(userReviewMapper.toResponse(any())).thenAnswer(invocation ->
                    new UserReviewResponse(invocation.<UserReview>getArgument(0).getId(), invocation.<UserReview>getArgument(0).getTitle(), null, null, userId, "mockUsername", null, null)
            );

            PageResponse<UserReviewResponse> result = userReviewService.findReviewsByUser(userId, 0, 10, auth);

            assertEquals(2, result.getContent().size());
        }

        @Test
        @DisplayName("Should return review for correct movie")
        void shouldReturnReviewForCorrectMovie() {
            Long movieId = 1L, reviewId = 10L;
            Movie movie = Movie.builder().id(movieId).build();
            UserReview review = UserReview.builder().id(reviewId).title("Matching Review").movie(movie).build();
            when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(userReviewMapper.toResponse(review)).thenReturn(
                    new UserReviewResponse(reviewId, "Matching Review", null, null, null, null, movieId, null)
            );

            UserReviewResponse result = userReviewService.findReviewByIdForMovie(movieId, reviewId);

            assertEquals("Matching Review", result.title());
        }

        @Test
        @DisplayName("Should return review when it exists")
        void shouldReturnReview_WhenReviewExists() {
            Long reviewId = 1L;
            UserReview review = UserReview.builder().id(reviewId).title("Test Review").build();
            UserReviewResponse response = new UserReviewResponse(reviewId, "Test Review", null, null, null, null, null, null);
            when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(userReviewMapper.toResponse(review)).thenReturn(response);

            UserReviewResponse result = userReviewService.findReviewById(reviewId);

            assertEquals("Test Review", result.title());
        }
    }

    // === EXCEPTION SCENARIOS ===

    @Nested
    @DisplayName("Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw SecurityException when accessing another user's reviews")
        void shouldThrowExceptionWhenUserTriesToAccessAnotherUserReviews() {
            Authentication auth = mockAuthenticationWithEmail(DEFAULT_EMAIL);

            assertThrows(SecurityException.class, () -> userReviewService.findReviewsByUser(999L, 0, 10, auth));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when review does not match movie")
        void shouldThrowExceptionWhenReviewDoesNotMatchMovie() {
            Long movieId = 1L, reviewId = 10L;
            Movie otherMovie = Movie.builder().id(2L).build();
            UserReview review = UserReview.builder().id(reviewId).movie(otherMovie).build();
            when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            assertThrows(EntityNotFoundException.class, () -> userReviewService.findReviewByIdForMovie(movieId, reviewId));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when review does not exist")
        void shouldThrowException_WhenReviewDoesNotExist() {
            when(userReviewRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userReviewService.findReviewById(99L));
        }

        @Test
        @DisplayName("Should throw IllegalStateException if user already submitted review")
        void shouldThrowExceptionIfReviewAlreadyExists() {
            Authentication auth = mockAuthenticationWithEmail(DEFAULT_EMAIL);
            Long userId = 1L, movieId = 2L;

            when(movieRepository.findById(movieId)).thenReturn(Optional.of(new Movie()));
            when(userReviewRepository.findByUserIdAndMovieId(userId, movieId))
                    .thenReturn(Optional.of(UserReview.builder().build()));

            assertThrows(IllegalStateException.class, () ->
                    userReviewService.saveReview(movieId, new UserReviewRequest("Another", "Body", 4), auth));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when deleting review of another user or wrong movie")
        void shouldThrowExceptionWhenUserIsNotOwnerOrMovieIdMismatch() {
            Authentication auth = mockAuthenticationWithEmail(DEFAULT_EMAIL);
            Long reviewId = 10L, wrongMovieId = 999L;

            User anotherUser = User.builder().id(2L).build();
            Movie movie = Movie.builder().id(1L).build();
            UserReview review = UserReview.builder().id(reviewId).user(anotherUser).movie(movie).build();
            when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            assertThrows(EntityNotFoundException.class, () ->
                    userReviewService.deleteReview(wrongMovieId, reviewId, auth));
        }
    }

    // === SAVE REVIEW ===

    @Nested
    @DisplayName("Save Review")
    class SaveReview {

        @Test
        @DisplayName("Should save review successfully")
        void shouldSaveReviewSuccessfully() {
            Authentication auth = mockAuthenticationWithEmail(DEFAULT_EMAIL);
            Long userId = 1L, movieId = 2L;

            UserReviewRequest request = new UserReviewRequest("Great movie", "Loved it", 5);
            Movie movie = Movie.builder().id(movieId).build();
            UserReview review = UserReview.builder().id(1L).title("Great movie").build();
            UserReviewResponse response = new UserReviewResponse(1L, "Great movie", "Loved it", 5, userId, "mockUsername", movieId, "Mock Movie");

            when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
            when(userReviewRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());
            when(userReviewMapper.toUserReview(any(), any(), any())).thenReturn(review);
            when(userReviewRepository.save(review)).thenReturn(review);
            when(userReviewMapper.toResponse(review)).thenReturn(response);

            UserReviewResponse result = userReviewService.saveReview(movieId, request, auth);

            assertNotNull(result);
            assertEquals("Great movie", result.title());
        }
    }

    // === DELETE REVIEW ===

    @Nested
    @DisplayName("Delete Review")
    class DeleteReview {

        @Test
        @DisplayName("Should delete review successfully when user is owner")
        void shouldDeleteReviewSuccessfullyWhenUserIsOwner() {
            Authentication auth = mockAuthenticationWithEmail(DEFAULT_EMAIL);
            Long movieId = 2L, reviewId = 10L;

            Movie movie = Movie.builder().id(movieId).build();
            UserReview review = UserReview.builder().id(reviewId).user(User.builder().id(1L).build()).movie(movie).build();
            when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

            assertDoesNotThrow(() -> userReviewService.deleteReview(movieId, reviewId, auth));
        }
    }
}