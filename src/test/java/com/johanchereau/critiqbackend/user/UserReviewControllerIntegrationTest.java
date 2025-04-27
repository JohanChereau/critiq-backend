package com.johanchereau.critiqbackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johanchereau.critiqbackend.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static com.johanchereau.critiqbackend.util.MockMvcRequestHelper.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Tag("Integration")
@DisplayName("Integration Tests - UserReviewController")
class UserReviewControllerIntegrationTest {

    private static final String MOVIE_REVIEWS_URL = "/movies/{movieId}/reviews";
    private static final String MOVIE_REVIEW_BY_ID_URL = "/movies/{movieId}/reviews/{reviewId}";
    private static final String REVIEWS_BY_USER_URL = "/users/{userId}/reviews";
    private static final String REVIEW_URL = "/reviews/{reviewId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private Long movieId;
    private Long userId;
    private Long otherUserId;
    private Long reviewId;

    @BeforeEach
    void setUp() {
        var movie = testDataFactory.createDefaultMovie();
        var user = testDataFactory.createDefaultUser();
        var other = testDataFactory.createDefaultOtherUser();
        var review = testDataFactory.createDefaultReview(user, movie);

        movieId = movie.getId();
        userId = user.getId();
        otherUserId = other.getId();
        reviewId = review.getId();
    }

    @Nested
    @DisplayName("Find Reviews By Movie")
    class FindReviewsByMovie {
        @Test
        @WithMockUser
        @DisplayName("Should return reviews for a given movie with pagination info")
        void shouldReturnReviewsForGivenMovie() throws Exception {
            String url = MOVIE_REVIEWS_URL.replace("{movieId}", movieId.toString());

            performGetPaginated(mockMvc, url)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
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
            String url = REVIEWS_BY_USER_URL.replace("{userId}", userId.toString());

            performGetPaginated(mockMvc, url)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser(username = TestDataFactory.OTHER_EMAIL)
        @DisplayName("Should forbid access to another user's reviews")
        void shouldForbidAccessToOtherUserReviews() throws Exception {
            String url = REVIEWS_BY_USER_URL.replace("{userId}", userId.toString());

            performGetPaginated(mockMvc, url)
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
            String url = MOVIE_REVIEW_BY_ID_URL
                    .replace("{movieId}", movieId.toString())
                    .replace("{reviewId}", reviewId.toString());

            performGet(mockMvc, url)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist or mismatch movie")
        void shouldReturn404WhenReviewInvalid() throws Exception {
            // not found
            String urlNotExist = MOVIE_REVIEW_BY_ID_URL
                    .replace("{movieId}", movieId.toString())
                    .replace("{reviewId}", "9999");

            performGet(mockMvc, urlNotExist)
                    .andExpect(status().isNotFound());

            // mismatch
            var anotherMovie = testDataFactory.createMovie("Inception", LocalDate.of(2010, 7, 16));
            String urlMismatch = MOVIE_REVIEW_BY_ID_URL
                    .replace("{movieId}", anotherMovie.getId().toString())
                    .replace("{reviewId}", reviewId.toString());

            performGet(mockMvc, urlMismatch)
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
            String url = REVIEW_URL.replace("{reviewId}", reviewId.toString());

            performGet(mockMvc, url)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(TestDataFactory.DEFAULT_REVIEW_TITLE));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            String url = REVIEW_URL.replace("{reviewId}", "9999");

            performGet(mockMvc, url)
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
            var newMovie = testDataFactory.createMovie("New Movie", LocalDate.of(2025, 1, 1));
            String url = MOVIE_REVIEWS_URL.replace("{movieId}", newMovie.getId().toString());

            var request = Map.of(
                    "title", "Masterpiece",
                    "body", TestDataFactory.DEFAULT_REVIEW_BODY,
                    "rating", 5
            );

            performPost(mockMvc, url, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Masterpiece"))
                    .andExpect(jsonPath("$.body").value(TestDataFactory.DEFAULT_REVIEW_BODY));
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should fail with 400 when request is invalid")
        void shouldFailWhenRequestInvalid() throws Exception {
            String url = MOVIE_REVIEWS_URL.replace("{movieId}", movieId.toString());
            var invalid = Map.of("title", "", "rating", 6);

            performPost(mockMvc, url, invalid)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors").exists());
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should fail if review already exists")
        void shouldFailIfReviewAlreadyExists() throws Exception {
            String url = MOVIE_REVIEWS_URL.replace("{movieId}", movieId.toString());
            var dup = Map.of("title", "Duplicate Review", "body", "Again!", "rating", 4);

            performPost(mockMvc, url, dup)
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
            String url = MOVIE_REVIEW_BY_ID_URL
                    .replace("{movieId}", movieId.toString())
                    .replace("{reviewId}", reviewId.toString());

            performDelete(mockMvc, url)
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("Should return 404 when deleting non-existent review")
        void shouldReturn404WhenDeletingNonExistingReview() throws Exception {
            String url = MOVIE_REVIEW_BY_ID_URL
                    .replace("{movieId}", movieId.toString())
                    .replace("{reviewId}", "9999");

            performDelete(mockMvc, url)
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = TestDataFactory.OTHER_EMAIL)
        @DisplayName("Should return 404 when deleting another user's review")
        void shouldReturn404WhenDeletingAnotherUsersReview() throws Exception {
            String url = MOVIE_REVIEW_BY_ID_URL
                    .replace("{movieId}", movieId.toString())
                    .replace("{reviewId}", reviewId.toString());

            performDelete(mockMvc, url)
                    .andExpect(status().isNotFound());
        }
    }
}