package com.johanchereau.critiqbackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import com.johanchereau.critiqbackend.util.TestUserFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.johanchereau.critiqbackend.util.MockMvcRequestHelper.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserReviewController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("Integration")
@DisplayName("Integration Tests - UserReviewController")
class UserReviewControllerIntegrationTest {

    private static final String DEFAULT_EMAIL = "john.doe@example.com";
    private static final String OTHER_EMAIL = "other.user@example.com";
    private static final String DEFAULT_MOVIE_TITLE = "Interstellar";
    private static final String DEFAULT_REVIEW_TITLE = "Amazing movie";
    private static final String DEFAULT_REVIEW_BODY = "One of the best movies ever!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long movieId;
    private Long reviewId;
    private Long userId;
    private Long otherUserId;

    @BeforeEach
    void setUp() {
        userReviewRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        movieId = createAndSaveMovie(DEFAULT_MOVIE_TITLE, LocalDate.of(2014, 11, 7));
        userId = createAndSaveUser("johndoe", DEFAULT_EMAIL, List.of());
        otherUserId = createAndSaveUser("otheruser", OTHER_EMAIL, List.of());

        reviewId = createAndSaveReview(userId, movieId, DEFAULT_REVIEW_TITLE, 5);
    }

    private Long createAndSaveMovie(String title, LocalDate releaseDate) {
        Movie movie = Movie.builder()
                .title(title)
                .releaseDate(releaseDate)
                .build();
        return movieRepository.save(movie).getId();
    }

    private Long createAndSaveUser(String username, String email, List<UserRole> roles) {
        User user = TestUserFactory.createUser(username, email, roles);
        return userRepository.save(user).getId();
    }

    private Long createAndSaveReview(Long userId, Long movieId, String title, int rating) {
        User user = userRepository.findById(userId).orElseThrow();
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        UserReview review = UserReview.builder()
                .user(user)
                .movie(movie)
                .title(title)
                .rating(rating)
                .build();
        return userReviewRepository.save(review).getId();
    }

    @Nested
    @DisplayName("Find Reviews By Movie")
    class FindReviewsByMovie {

        @Test
        @WithMockUser
        @DisplayName("Should return reviews for a given movie")
        void shouldReturnReviewsForGivenMovie() throws Exception {
            performGetPaginated(mockMvc, "/movies/" + movieId + "/reviews")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].title").value(DEFAULT_REVIEW_TITLE));
        }
    }

    @Nested
    @DisplayName("Find Reviews By User")
    class FindReviewsByUser {

        @Test
        @WithMockUser(username = DEFAULT_EMAIL)
        @DisplayName("Should return reviews for the connected user")
        void shouldReturnReviewsForConnectedUser() throws Exception {
            performGetPaginated(mockMvc, "/users/" + userId + "/reviews")
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].title").value(DEFAULT_REVIEW_TITLE))
                    .andExpect(jsonPath("$.content[0].rating").value(5));
        }

        @Test
        @WithMockUser(username = OTHER_EMAIL)
        @DisplayName("Should forbid access to another user's reviews")
        void shouldForbidAccessToOtherUserReviews() throws Exception {
            performGetPaginated(mockMvc, "/users/" + userId + "/reviews")
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("Find Review By ID For Movie")
    class FindReviewByIdForMovie {

        @Test
        @WithMockUser
        @DisplayName("Should return a review when it exists for the movie")
        void shouldReturnReviewForMovie() throws Exception {
            performGet(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.title").value(DEFAULT_REVIEW_TITLE))
                    .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            performGet(mockMvc, "/movies/" + movieId + "/reviews/9999")
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not belong to the movie")
        void shouldReturn404WhenReviewNotBelongsToMovie() throws Exception {
            Long anotherMovieId = createAndSaveMovie("Inception", LocalDate.of(2010, 7, 16));
            performGet(mockMvc, "/movies/" + anotherMovieId + "/reviews/" + reviewId)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Find Review By ID")
    class FindReviewById {

        @Test
        @WithMockUser
        @DisplayName("Should return a review when it exists")
        void shouldReturnReview() throws Exception {
            performGet(mockMvc, "/reviews/" + reviewId)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.title").value(DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            performGet(mockMvc, "/reviews/9999")
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Add Review")
    class AddReview {

        @Test
        @WithMockUser(username = DEFAULT_EMAIL)
        @DisplayName("Should successfully add a review for a movie")
        void shouldAddReviewSuccessfully() throws Exception {
            Long newMovieId = createAndSaveMovie("New Movie", LocalDate.of(2025, 1, 1));

            var request = new UserReviewRequest(
                    "Masterpiece",
                    DEFAULT_REVIEW_BODY,
                    5
            );

            performPost(mockMvc, "/movies/" + newMovieId + "/reviews", request)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.title").value("Masterpiece"))
                    .andExpect(jsonPath("$.rating").value(5))
                    .andExpect(jsonPath("$.body").value(DEFAULT_REVIEW_BODY));
        }

        @Test
        @WithMockUser(username = DEFAULT_EMAIL)
        @DisplayName("Should fail with 400 when request is invalid")
        void shouldFailWhenRequestInvalid() throws Exception {
            var invalidRequest = new UserReviewRequest("", null, 6);

            performPost(mockMvc, "/movies/" + movieId + "/reviews", invalidRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.validationErrors").exists());
        }

        @Test
        @WithMockUser(username = DEFAULT_EMAIL)
        @DisplayName("Should fail with 400 if user already added a review for the movie")
        void shouldFailIfReviewAlreadyExists() throws Exception {
            var request = new UserReviewRequest("Another review", "Trying again!", 4);

            performPost(mockMvc, "/movies/" + movieId + "/reviews", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorMessage").value("You have already submitted a review for this movie"));
        }
    }

    @Nested
    @DisplayName("Delete Review")
    class DeleteReview {

        @Test
        @WithMockUser(username = DEFAULT_EMAIL)
        @DisplayName("Should delete the user's own review successfully")
        void shouldDeleteOwnReviewSuccessfully() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = DEFAULT_EMAIL)
        @DisplayName("Should return 404 when trying to delete a non-existing review")
        void shouldReturn404WhenDeletingNonExistingReview() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/9999")
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = OTHER_EMAIL)
        @DisplayName("Should return 404 when trying to delete another user's review")
        void shouldReturn404WhenDeletingAnotherUsersReview() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isNotFound());
        }
    }
}