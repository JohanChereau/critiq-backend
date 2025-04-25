package com.johanchereau.critiqbackend.user;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class UserReviewServiceTest {

    private AutoCloseable mocks;

    @Mock
    private UserReviewRepository userReviewRepository;

    @Mock
    private MovieRepository movieRepository;

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

    // ========================
    // === FIND OPERATIONS ===
    // ========================

    @Test
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
    void shouldReturnEmptyPageWhenNoReviewsForMovie() {
        when(userReviewRepository.findByMovieId(eq(99L), any())).thenReturn(new PageImpl<>(List.of()));
        PageResponse<UserReviewResponse> result = userReviewService.findReviewsByMovie(99L, 0, 10);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void shouldReturnUserReviewsWhenUserIsOwner() {
        Long userId = 1L;
        User mockUser = User.builder().id(userId).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);

        List<UserReview> reviews = List.of(
                UserReview.builder().id(1L).title("Review 1").rating(4).user(mockUser).movie(new Movie()).build(),
                UserReview.builder().id(2L).title("Review 2").rating(5).user(mockUser).movie(new Movie()).build()
        );
        when(userReviewRepository.findByUserId(eq(userId), any())).thenReturn(new PageImpl<>(reviews));
        when(userReviewMapper.toResponse(any())).thenAnswer(invocation ->
                new UserReviewResponse(invocation.<UserReview>getArgument(0).getId(), invocation.<UserReview>getArgument(0).getTitle(), null, null, userId, "mockUsername", null, null)
        );

        PageResponse<UserReviewResponse> result = userReviewService.findReviewsByUser(userId, 0, 10, auth);

        assertEquals(2, result.getContent().size());
    }

    @Test
    void shouldReturnReviewForCorrectMovie() {
        Long movieId = 1L, reviewId = 10L;
        Movie movie = Movie.builder().id(movieId).build();
        UserReview review = UserReview.builder().id(reviewId).title("Matching Review").movie(movie).build();
        when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userReviewMapper.toResponse(review)).thenReturn(new UserReviewResponse(reviewId, "Matching Review", null, null, null, null, movieId, null));

        UserReviewResponse result = userReviewService.findReviewByIdForMovie(movieId, reviewId);

        assertEquals("Matching Review", result.title());
    }

    @Test
    void shouldReturnReview_WhenReviewExists() {
        Long reviewId = 1L;
        UserReview review = UserReview.builder().id(reviewId).title("Test Review").build();
        UserReviewResponse response = new UserReviewResponse(reviewId, "Test Review", null, null, null, null, null, null);
        when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userReviewMapper.toResponse(review)).thenReturn(response);

        UserReviewResponse result = userReviewService.findReviewById(reviewId);

        assertEquals("Test Review", result.title());
    }

    // =============================
    // === EXCEPTION SCENARIOS ====
    // =============================

    @Test
    void shouldThrowExceptionWhenUserTriesToAccessAnotherUserReviews() {
        Long requestedUserId = 1L, connectedUserId = 2L;
        User mockUser = User.builder().id(connectedUserId).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);

        assertThrows(SecurityException.class, () -> userReviewService.findReviewsByUser(requestedUserId, 0, 10, auth));
    }

    @Test
    void shouldThrowExceptionWhenReviewDoesNotMatchMovie() {
        Long movieId = 1L, reviewId = 10L;
        Movie otherMovie = Movie.builder().id(2L).build();
        UserReview review = UserReview.builder().id(reviewId).movie(otherMovie).build();
        when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        assertThrows(EntityNotFoundException.class, () -> userReviewService.findReviewByIdForMovie(movieId, reviewId));
    }

    @Test
    void shouldThrowException_WhenReviewDoesNotExist() {
        when(userReviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userReviewService.findReviewById(99L));
    }

    @Test
    void shouldThrowExceptionIfReviewAlreadyExists() {
        Long userId = 1L;
        Long movieId = 2L;
        User mockUser = User.builder().id(userId).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(new Movie()));
        when(userReviewRepository.findByUserIdAndMovieId(userId, movieId))
                .thenReturn(Optional.of(UserReview.builder().build()));

        Runnable call = () -> userReviewService.saveReview(movieId, new UserReviewRequest("Another", "Body", 4), auth);

        assertThrows(IllegalStateException.class, call::run);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwnerOrMovieIdMismatch() {
        Long reviewId = 10L, wrongMovieId = 999L, userId = 1L;
        User actualUser = User.builder().id(2L).build();
        Movie movie = Movie.builder().id(1L).build();
        UserReview review = UserReview.builder().id(reviewId).user(actualUser).movie(movie).build();
        User mockUser = User.builder().id(userId).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mockUser);
        when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        assertThrows(EntityNotFoundException.class, () -> userReviewService.deleteReview(wrongMovieId, reviewId, auth));
    }

    // ============================
    // === CREATE / SAVE LOGIC ===
    // ============================

    @Test
    void shouldSaveReviewSuccessfully() {
        Long userId = 1L, movieId = 2L;
        User user = User.builder().id(userId).build();
        Movie movie = Movie.builder().id(movieId).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        UserReviewRequest request = new UserReviewRequest("Great movie", "Loved it", 5);
        UserReview review = UserReview.builder().id(1L).title("Great movie").user(user).movie(movie).build();
        UserReviewResponse response = new UserReviewResponse(1L, "Great movie", "Loved it", 5, userId, "mockUsername", movieId, "Mock Movie");

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(userReviewRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());
        when(userReviewMapper.toUserReview(request, user, movie)).thenReturn(review);
        when(userReviewRepository.save(review)).thenReturn(review);
        when(userReviewMapper.toResponse(review)).thenReturn(response);

        UserReviewResponse result = userReviewService.saveReview(movieId, request, auth);

        assertNotNull(result);
        assertEquals("Great movie", result.title());
    }

    // ===========================
    // === DELETE LOGIC TESTS ===
    // ===========================

    @Test
    void shouldDeleteReviewSuccessfullyWhenUserIsOwner() {
        Long userId = 1L, movieId = 2L, reviewId = 10L;
        User user = User.builder().id(userId).build();
        Movie movie = Movie.builder().id(movieId).build();
        UserReview review = UserReview.builder().id(reviewId).user(user).movie(movie).build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(userReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        assertDoesNotThrow(() -> userReviewService.deleteReview(movieId, reviewId, auth));
    }
}