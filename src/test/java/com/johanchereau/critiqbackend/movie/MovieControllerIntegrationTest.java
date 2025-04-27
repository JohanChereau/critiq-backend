package com.johanchereau.critiqbackend.movie;

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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Tag("Integration")
@DisplayName("Integration Tests - MovieController")
class MovieControllerIntegrationTest {

    private static final String BASE_URL = "/movies";
    private static final String MOVIE_ID_URL = "/movies/{movieId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private String defaultUserEmail;

    @BeforeEach
    void setUp() {
        // Ensure default user exists for authentication
        var user = testDataFactory.createDefaultUser();
        defaultUserEmail = TestDataFactory.DEFAULT_EMAIL;
    }

    @Nested
    @DisplayName("Add Movie")
    class AddMovie {

        @Test
        @WithMockUser(username = "john.doe@example.com")
        @DisplayName("Should successfully add a movie")
        void shouldAddMovieSuccessfully() throws Exception {
            var request = Map.of(
                    "title", "Dune",
                    "description", "Epic sci-fi",
                    "storyline", "A hero rises...",
                    "tagline", "Fear is the mind-killer",
                    "releaseDate", "2021-10-22",
                    "budgetEur", 165000000,
                    "classification", "PG-13",
                    "durationMinutes", 155,
                    "posterUrl", "http://.../dune.jpg",
                    "bannerUrl", "http://.../dune-banner.jpg"
            );

            performPost(mockMvc, BASE_URL, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.title").value("Dune"))
                    .andExpect(jsonPath("$.releaseDate").value("2021-10-22"));
        }

        @Test
        @DisplayName("Should return 403 when unauthenticated")
        void shouldReturn403WhenUnauthenticated() throws Exception {
            var request = Map.of("title", "Test", "releaseDate", "2025-01-01", "durationMinutes", 100);
            performPost(mockMvc, BASE_URL, request)
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Find Movie By ID")
    class FindMovieById {

        @Test
        @WithMockUser
        @DisplayName("Should return movie when it exists")
        void shouldReturnMovie() throws Exception {
            var movie = testDataFactory.createMovie("Blade Runner", LocalDate.of(1982, 6, 25));
            String url = MOVIE_ID_URL.replace("{movieId}", movie.getId().toString());

            performGet(mockMvc, url)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(movie.getId()))
                    .andExpect(jsonPath("$.title").value("Blade Runner"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when movie not found")
        void shouldReturn404WhenNotFound() throws Exception {
            String url = MOVIE_ID_URL.replace("{movieId}", "9999");
            performGet(mockMvc, url)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Find All Movies")
    class FindAllMovies {

        @Test
        @WithMockUser
        @DisplayName("Should return paginated list of movies")
        void shouldReturnPaginatedList() throws Exception {
            // Create two movies
            var m1 = testDataFactory.createMovie("Movie A", LocalDate.of(2020, 1, 1));
            var m2 = testDataFactory.createMovie("Movie B", LocalDate.of(2021, 1, 1));

            // Expect exactly 2 movies
            performGetPaginated(mockMvc, BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[*].title", containsInAnyOrder("Movie A", "Movie B")));
        }
    }
}