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

    @BeforeEach
    void setUp() {
        // Persist both a regular user and an admin user
        testDataFactory.createDefaultUser();
        testDataFactory.createDefaultAdmin();
    }

    @Nested
    @DisplayName("Add Movie")
    class AddMovie {
        @Test
        @WithMockUser(username = TestDataFactory.ADMIN_EMAIL, roles = "ADMIN")
        @DisplayName("Admin can add a movie")
        void adminCanAdd() throws Exception {
            var req = Map.of(
                    "title", "Dune",
                    "releaseDate", "2021-10-22",
                    "durationMinutes", 155,
                    "budgetEur", 160000000,
                    "classification", "PG-13"
            );
            performPost(mockMvc, BASE_URL, req)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.title").value("Dune"));
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("User cannot add a movie")
        void userCannotAdd() throws Exception {
            var req = Map.of(
                    "title", "X",
                    "releaseDate", "2025-01-01",
                    "durationMinutes", 120,
                    "budgetEur", 100000,
                    "classification", "PG"
            );
            performPost(mockMvc, BASE_URL, req)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Unauthenticated cannot add")
        void unauthCannotAdd() throws Exception {
            var req = Map.of(
                    "title", "X",
                    "releaseDate", "2025-01-01",
                    "durationMinutes", 120,
                    "budgetEur", 100000,
                    "classification", "PG"
            );
            performPost(mockMvc, BASE_URL, req)
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Update Movie")
    class UpdateMovie {
        @Test
        @WithMockUser(username = TestDataFactory.ADMIN_EMAIL, roles = "ADMIN")
        @DisplayName("Admin can update existing movie")
        void adminCanUpdate() throws Exception {
            var movie = testDataFactory.createMovie("Original", LocalDate.of(2020, 1, 1));
            String url = MOVIE_ID_URL.replace("{movieId}", movie.getId().toString());
            var req = Map.of(
                    "title", "Updated",
                    "releaseDate", movie.getReleaseDate().toString(),
                    "durationMinutes", 120,
                    "budgetEur", 500000,
                    "classification", "PG"
            );
            performPut(mockMvc, url, req)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated"));
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("User cannot update movie")
        void userCannotUpdate() throws Exception {
            var movie = testDataFactory.createMovie("X", LocalDate.now());
            String url = MOVIE_ID_URL.replace("{movieId}", movie.getId().toString());
            var req = Map.of(
                    "title", "Fail",
                    "releaseDate", LocalDate.now().toString(),
                    "durationMinutes", 120,
                    "budgetEur", 100000,
                    "classification", "PG"
            );
            performPut(mockMvc, url, req)
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = TestDataFactory.ADMIN_EMAIL, roles = "ADMIN")
        @DisplayName("Updating non-existent movie returns 404")
        void updateNotFound() throws Exception {
            String url = MOVIE_ID_URL.replace("{movieId}", "9999");
            var req = Map.of(
                    "title", "X",
                    "releaseDate", LocalDate.now().toString(),
                    "durationMinutes", 120,
                    "budgetEur", 100000,
                    "classification", "PG"
            );
            performPut(mockMvc, url, req)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Delete Movie")
    class DeleteMovie {
        @Test
        @WithMockUser(username = TestDataFactory.ADMIN_EMAIL, roles = "ADMIN")
        @DisplayName("Admin can delete movie")
        void adminCanDelete() throws Exception {
            var movie = testDataFactory.createMovie("ToDelete", LocalDate.now());
            String url = MOVIE_ID_URL.replace("{movieId}", movie.getId().toString());
            performDelete(mockMvc, url)
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = TestDataFactory.DEFAULT_EMAIL)
        @DisplayName("User cannot delete movie")
        void userCannotDelete() throws Exception {
            var movie = testDataFactory.createMovie("X", LocalDate.now());
            String url = MOVIE_ID_URL.replace("{movieId}", movie.getId().toString());
            performDelete(mockMvc, url)
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = TestDataFactory.ADMIN_EMAIL, roles = "ADMIN")
        @DisplayName("Deleting non-existent movie returns 404")
        void deleteNotFound() throws Exception {
            String url = MOVIE_ID_URL.replace("{movieId}", "9999");
            performDelete(mockMvc, url)
                    .andExpect(status().isNotFound());
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
            var m1 = testDataFactory.createMovie("Movie A", LocalDate.of(2020, 1, 1));
            var m2 = testDataFactory.createMovie("Movie B", LocalDate.of(2021, 1, 1));
            performGetPaginated(mockMvc, BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content[*].title", containsInAnyOrder("Movie A", "Movie B")));
        }
    }
}