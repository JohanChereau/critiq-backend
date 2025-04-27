package com.johanchereau.critiqbackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import com.johanchereau.critiqbackend.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.johanchereau.critiqbackend.util.MockMvcRequestHelper.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link UserReviewController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("Integration")
@DisplayName("Integration Tests - UserReviewController")
class UserReviewControllerIntegrationTest {

    private static final long NON_EXISTENT_REVIEW_ID = 9999L;
    private static final String NEW_MOVIE_TITLE = "New Movie";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserReviewRepository userReviewRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private Long movieId;
    private Long reviewId;
    private Long userId;
    private Long otherUserId;

    private void cleanDatabase() {
        userReviewRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    @DisplayName("Clean database and initialize test data")
    void setUp() {
        cleanDatabase();

        Movie movie = testDataFactory.createDefaultMovie();
        User user = testDataFactory.createDefaultUser();
        User otherUser = testDataFactory.createDefaultOtherUser();
        UserReview review = testDataFactory.createDefaultReview(user, movie);

        movieId = movie.getId();
        userId = user.getId();
        otherUserId = otherUser.getId();
        reviewId = review.getId();
    }

    // === Tests ===

    @Nested
    @DisplayName("Find Reviews By Movie")
    class FindReviewsByMovie {

        @Test
        @WithMockUser
        @DisplayName("Should return reviews for a given movie")
        void shouldReturnReviewsForGivenMovie() throws Exception {
            performGetPaginated(mockMvc, "/movies/" + movieId + "/reviews")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }
    }

    @Nested
    @DisplayName("Find Reviews By User")
    class FindReviewsByUser {

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should return reviews for the connected user")
        void shouldReturnReviewsForConnectedUser() throws Exception {
            performGetPaginated(mockMvc, "/users/" + userId + "/reviews")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser(username = TestDataFactory.OTHER_EMAIL)
        @DisplayName("Should forbid access to another user's reviews")
        void shouldForbidAccessToOtherUserReviews() throws Exception {
            performGetPaginated(mockMvc, "/users/" + userId + "/reviews")
                    .andExpect(status().isForbidden());
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
                    .andExpect(jsonPath("$.title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            performGet(mockMvc, "/movies/" + movieId + "/reviews/" + NON_EXISTENT_REVIEW_ID)
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not belong to the movie")
        void shouldReturn404WhenReviewNotBelongsToMovie() throws Exception {
            Long anotherMovieId = testDataFactory.createMovie("Inception", LocalDate.of(2010, 7, 16)).getId();
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
                    .andExpect(jsonPath("$.title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            performGet(mockMvc, "/reviews/" + NON_EXISTENT_REVIEW_ID)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Add Review")
    class AddReview {

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should successfully add a review for a new movie")
        void shouldAddReviewSuccessfully() throws Exception {
            Long newMovieId = testDataFactory.createMovie(NEW_MOVIE_TITLE, LocalDate.of(2025, 1, 1)).getId();

            var request = new UserReviewRequest(
                    "Masterpiece",
                    TestDataFactory.DEFAULT_REVIEW_BODY,
                    5
            );

            performPost(mockMvc, "/movies/" + newMovieId + "/reviews", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Masterpiece"))
                    .andExpect(jsonPath("$.body").value(TestDataFactory.DEFAULT_REVIEW_BODY));
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should fail with 400 when request is invalid")
        void shouldFailWhenRequestInvalid() throws Exception {
            var invalidRequest = new UserReviewRequest("", null, 6);

            performPost(mockMvc, "/movies/" + movieId + "/reviews", invalidRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors").exists());
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should fail with 400 if user already added a review for the movie")
        void shouldFailIfReviewAlreadyExists() throws Exception {
            var request = new UserReviewRequest("Duplicate Review", "Trying again!", 4);

            performPost(mockMvc, "/movies/" + movieId + "/reviews", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorMessage").value("You have already submitted a review for this movie"));
        }
    }

    @Nested
    @DisplayName("Delete Review")
    class DeleteReview {

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should delete own review successfully")
        void shouldDeleteOwnReviewSuccessfully() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should return 404 when trying to delete non-existing review")
        void shouldReturn404WhenDeletingNonExistingReview() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + NON_EXISTENT_REVIEW_ID)
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = TestDataFactory.OTHER_EMAIL)
        @DisplayName("Should return 404 when trying to delete another user's review")
        void shouldReturn404WhenDeletingAnotherUsersReview() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isNotFound());
        }
    }
}