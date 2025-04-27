package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.user.User;
import com.johanchereau.critiqbackend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@Tag("Unit")
@DisplayName("Unit Tests - MovieService")
class MovieServiceTest {

    private static final String USER_EMAIL = "user@example.com";
    @Mock
    private UserRepository userRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieMapper movieMapper;
    @InjectMocks
    private MovieService movieService;
    @Mock
    private Authentication authentication;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(42L).email(USER_EMAIL).build();
        lenient().when(authentication.getName()).thenReturn(USER_EMAIL);
        lenient().when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
    }

    @Nested
    @DisplayName("save()")
    class SaveMovie {
        @Test
        @DisplayName("should save a new movie when user exists")
        void shouldSaveMovieWhenUserExists() {
            MovieRequest request = new MovieRequest(
                    null, "Inception", "Dream within a dream", "Story", "Tagline",
                    LocalDate.of(2010, 7, 16), 160_000_000, "PG-13", 148,
                    "poster.jpg", "banner.jpg"
            );
            Movie toSave = Movie.builder()
                    .title("Inception").releaseDate(request.releaseDate())
                    .build();
            Movie saved = Movie.builder()
                    .id(100L).title("Inception").releaseDate(request.releaseDate())
                    .build();

            given(movieMapper.toMovie(request)).willReturn(toSave);
            given(movieRepository.save(toSave)).willReturn(saved);

            Movie result = movieService.save(request, authentication);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals("Inception", result.getTitle());
            then(userRepository).should().findByEmail(USER_EMAIL);
            then(movieRepository).should().save(toSave);
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldThrowWhenUserNotFound() {
            given(userRepository.findByEmail(USER_EMAIL)).willReturn(Optional.empty());
            MovieRequest req = new MovieRequest(
                    null, "T", "d", "s", "t", LocalDate.now(), 0, "R", 90, null, null
            );

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> movieService.save(req, authentication)
            );
            assertEquals("User not found", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateMovie {
        @Test
        @DisplayName("should update movie when exists and user is admin")
        void shouldUpdateMovie() {
            // given
            long id = 5L;
            MovieRequest req = new MovieRequest(
                    id, "Up", "d", "s", "t", LocalDate.of(2005, 1, 1), 0, null, 0, null, null
            );
            Movie existing = Movie.builder().id(id).title("Old").releaseDate(LocalDate.of(2000, 1, 1)).build();
            given(movieRepository.findById(id)).willReturn(Optional.of(existing));
            given(movieRepository.save(existing)).willReturn(existing);

            // when
            Movie result = movieService.update(id, req, authentication);

            // then
            assertEquals(id, result.getId());
            assertEquals("Up", result.getTitle());
            then(movieRepository).should().save(existing);
        }

        @Test
        @DisplayName("should throw when movie not found")
        void shouldThrowIfNotFound() {
            given(movieRepository.findById(99L)).willReturn(Optional.empty());
            MovieRequest req = new MovieRequest(
                    99L, "X", "d", "s", "t", LocalDate.now(), 0, null, 0, null, null
            );
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> movieService.update(99L, req, authentication)
            );
            assertTrue(ex.getMessage().contains("No movie find with the id: 99"));
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteMovie {
        @Test
        @DisplayName("should delete movie when exists and user is admin")
        void shouldDelete() {
            long id = 8L;
            Movie m = Movie.builder().id(id).build();
            given(movieRepository.findById(id)).willReturn(Optional.of(m));

            assertDoesNotThrow(() -> movieService.delete(id, authentication));
            then(movieRepository).should().delete(m);
        }

        @Test
        @DisplayName("should throw when not found")
        void shouldThrowIfMissing() {
            given(movieRepository.findById(123L)).willReturn(Optional.empty());
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> movieService.delete(123L, authentication)
            );
            assertTrue(ex.getMessage().contains("No movie find with the id: 123"));
        }
    }

    @Nested
    @DisplayName("findById() & findAllMovies()")
    class FindMethods {
        @Test
        @DisplayName("findById works")
        void findById() {
            long id = 7L;
            Movie mv = Movie.builder().id(id).title("M").releaseDate(LocalDate.now()).build();
            MovieResponse resp = MovieResponse.builder().id(id).title("M").build();
            given(movieRepository.findById(id)).willReturn(Optional.of(mv));
            given(movieMapper.toMovieResponse(mv)).willReturn(resp);

            MovieResponse out = movieService.findById(id);
            assertEquals("M", out.getTitle());
        }

        @Test
        @DisplayName("findAllMovies works")
        void findAllMovies() {
            Movie a = Movie.builder().id(1L).title("A").build();
            Movie b = Movie.builder().id(2L).title("B").build();
            Page<Movie> pg = new PageImpl<>(List.of(a, b),
                    PageRequest.of(0, 10, Sort.by("createdAt").descending()), 2
            );
            MovieResponse ra = MovieResponse.builder().id(1L).title("A").build();
            MovieResponse rb = MovieResponse.builder().id(2L).title("B").build();
            given(movieRepository.findAll(any(Pageable.class))).willReturn(pg);
            given(movieMapper.toMovieResponse(a)).willReturn(ra);
            given(movieMapper.toMovieResponse(b)).willReturn(rb);

            PageResponse<MovieResponse> res = movieService.findAllMovies(0, 10);
            assertEquals(2, res.getTotalElements());
            assertEquals(List.of(ra, rb), res.getContent());
        }
    }
}