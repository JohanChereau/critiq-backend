package com.johanchereau.critiqbackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import com.johanchereau.critiqbackend.util.TestUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.johanchereau.critiqbackend.util.MockMvcRequestHelper.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserReviewController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integration Tests - UserReviewController")
class UserReviewControllerIntegrationTest {

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

        movieId = createAndSaveMovie("Interstellar", LocalDate.of(2014, 11, 7));
        userId = createAndSaveUser("johndoe", "john.doe@example.com", List.of());
        otherUserId = createAndSaveUser("otheruser", "other.user@example.com", List.of());

        reviewId = createAndSaveReview(userId, movieId, "Amazing movie", 5);
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
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].title").value("Amazing movie"));
        }
    }

    @Nested
    @DisplayName("Find Reviews By User")
    class FindReviewsByUser {

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should return reviews for the connected user")
        void shouldReturnReviewsForConnectedUser() throws Exception {
            performGetPaginated(mockMvc, "/users/" + userId + "/reviews")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].title").value("Amazing movie"))
                    .andExpect(jsonPath("$.content[0].rating").value(5));
        }

        @Test
        @WithMockUser(username = "other.user@example.com")
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
                    .andExpect(jsonPath("$.title").value("Amazing movie"))
                    .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            Long nonExistingReviewId = 9999L;
            performGet(mockMvc, "/movies/" + movieId + "/reviews/" + nonExistingReviewId)
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not belong to the movie")
        void shouldReturn404WhenReviewNotBelongsToMovie() throws Exception {
            // Create a second movie
            Movie anotherMovie = Movie.builder()
                    .title("Inception")
                    .releaseDate(LocalDate.of(2010, 7, 16))
                    .build();
            anotherMovie = movieRepository.save(anotherMovie);
            Long anotherMovieId = anotherMovie.getId();

            // Try to get reviewId but with wrong movieId
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
                    .andExpect(jsonPath("$.title").value("Amazing movie"))
                    .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when the review does not exist")
        void shouldReturn404WhenReviewNotFound() throws Exception {
            Long nonExistingReviewId = 9999L;
            performGet(mockMvc, "/reviews/" + nonExistingReviewId)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Add Review")
    class AddReview {

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should successfully add a review for a movie")
        void shouldAddReviewSuccessfully() throws Exception {
            // Create a new movie to avoid the duplication constraint that leads to review duplication error
            Long newMovieId = createAndSaveMovie("New Movie", LocalDate.of(2025, 1, 1));

            var request = new UserReviewRequest(
                    "Masterpiece",
                    "One of the best movies ever!",
                    5
            );

            performPost(mockMvc, "/movies/" + newMovieId + "/reviews", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Masterpiece"))
                    .andExpect(jsonPath("$.rating").value(5))
                    .andExpect(jsonPath("$.body").value("One of the best movies ever!"));
        }

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should fail with 400 when request is invalid")
        void shouldFailWhenRequestInvalid() throws Exception {
            UserReviewRequest invalidRequest = new UserReviewRequest(
                    "",   // Empty title
                    null, // Body is optional
                    6    // Invalid rating
            );

            performPost(mockMvc, "/movies/" + movieId + "/reviews", invalidRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors").exists());
        }

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should fail with 400 if user already added a review for the movie")
        void shouldFailIfReviewAlreadyExists() throws Exception {
            var request = new UserReviewRequest(
                    "Another review",
                    "Trying to review again!",
                    4
            );

            performPost(mockMvc, "/movies/" + movieId + "/reviews", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(400))
                    .andExpect(jsonPath("$.errorMessage").value("You have already submitted a review for this movie"));
        }
    }

    @Nested
    @DisplayName("Delete Review")
    class DeleteReview {

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should delete the user's own review successfully")
        void shouldDeleteOwnReviewSuccessfully() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should return 404 when trying to delete a non-existing review")
        void shouldReturn404WhenDeletingNonExistingReview() throws Exception {
            Long nonExistingReviewId = 9999L;
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + nonExistingReviewId)
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "other.user@example.com")
        @DisplayName("Should return 404 when trying to delete another user's review")
        void shouldReturn404WhenDeletingAnotherUsersReview() throws Exception {
            performDelete(mockMvc, "/movies/" + movieId + "/reviews/" + reviewId)
                    .andExpect(status().isNotFound());
        }
    }
}