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
        // common authenticated user
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
            // given
            MovieRequest request = new MovieRequest(
                    null,
                    "Inception",
                    "Dream within a dream",
                    "Story",
                    "Tagline",
                    LocalDate.of(2010, 7, 16),
                    160_000_000,
                    "PG-13",
                    148,
                    "poster.jpg",
                    "banner.jpg"
            );
            Movie toSave = Movie.builder()
                    .title("Inception")
                    .releaseDate(request.releaseDate())
                    .build();
            Movie saved = Movie.builder()
                    .id(100L)
                    .title("Inception")
                    .releaseDate(request.releaseDate())
                    .build();

            given(movieMapper.toMovie(request)).willReturn(toSave);
            given(movieRepository.save(toSave)).willReturn(saved);

            // when
            Movie result = movieService.save(request, authentication);

            // then
            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals("Inception", result.getTitle());
            then(userRepository).should().findByEmail(USER_EMAIL);
            then(movieRepository).should().save(toSave);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when user not found")
        void shouldThrowWhenUserNotFound() {
            // given no user
            given(userRepository.findByEmail(USER_EMAIL)).willReturn(Optional.empty());
            MovieRequest request = new MovieRequest(
                    null, "Title", "desc", "story", "tag", LocalDate.now(), 0, "R", 90, null, null
            );

            // when / then
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> movieService.save(request, authentication)
            );
            assertEquals("User not found", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {
        @Test
        @DisplayName("should return MovieResponse when movie exists")
        void shouldReturnMovieResponse() {
            // given
            long id = 7L;
            Movie movie = Movie.builder().id(id).title("Memento").releaseDate(LocalDate.of(2000, 9, 5)).build();
            MovieResponse response = MovieResponse.builder()
                    .id(id)
                    .title("Memento")
                    .releaseDate(LocalDate.of(2000, 9, 5))
                    .build();

            given(movieRepository.findById(id)).willReturn(Optional.of(movie));
            given(movieMapper.toMovieResponse(movie)).willReturn(response);

            // when
            MovieResponse result = movieService.findById(id);

            // then
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("Memento", result.getTitle());
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when movie not found")
        void shouldThrowWhenNotFound() {
            // given
            given(movieRepository.findById(123L)).willReturn(Optional.empty());

            // when / then
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> movieService.findById(123L)
            );
            assertTrue(ex.getMessage().contains("No movie find with the id: 123"));
        }
    }

    @Nested
    @DisplayName("findAllMovies()")
    class FindAllMovies {
        @Test
        @DisplayName("should return paginated movies")
        void shouldReturnPaginatedMovies() {
            // given
            Movie m1 = Movie.builder().id(1L).title("A").releaseDate(LocalDate.now()).build();
            Movie m2 = Movie.builder().id(2L).title("B").releaseDate(LocalDate.now()).build();
            Page<Movie> page = new PageImpl<>(
                    List.of(m1, m2),
                    PageRequest.of(0, 10, Sort.by("createdAt").descending()),
                    2
            );
            MovieResponse r1 = MovieResponse.builder()
                    .id(1L)
                    .title("A")
                    .build();
            MovieResponse r2 = MovieResponse.builder()
                    .id(2L)
                    .title("B")
                    .build();

            given(movieRepository.findAll(any(Pageable.class))).willReturn(page);
            given(movieMapper.toMovieResponse(m1)).willReturn(r1);
            given(movieMapper.toMovieResponse(m2)).willReturn(r2);

            // when
            PageResponse<MovieResponse> rsp = movieService.findAllMovies(0, 10);

            // then
            assertEquals(2, rsp.getTotalElements());
            assertEquals(1, rsp.getTotalPages());
            assertTrue(rsp.isFirst());
            assertTrue(rsp.isLast());
            assertEquals(List.of(r1, r2), rsp.getContent());
            then(movieRepository).should().findAll(any(Pageable.class));
        }
    }
}