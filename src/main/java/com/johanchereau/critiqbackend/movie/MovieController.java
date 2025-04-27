package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("movies")
@RequiredArgsConstructor
@Tag(name = "Movie")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("{movie-id}")
    public ResponseEntity<MovieResponse> findMovieById(@PathVariable("movie-id") long movieId) {
        return ResponseEntity.ok(movieService.findById(movieId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MovieResponse>> findAllMovies(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(movieService.findAllMovies(page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> addMovie(
            @Valid @RequestBody MovieRequest movieRequest,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(movieService.save(movieRequest, connectedUser));
    }

    @PutMapping("{movie-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable("movie-id") long movieId,
            @Valid @RequestBody MovieRequest movieRequest,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(movieService.update(movieId, movieRequest, connectedUser));
    }

    @DeleteMapping("{movie-id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable("movie-id") long movieId,
            Authentication connectedUser
    ) {
        movieService.delete(movieId, connectedUser);
        return ResponseEntity.ok().build();
    }
}
