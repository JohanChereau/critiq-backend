package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movies")
@RequiredArgsConstructor
@Tag(name = "Movie")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<Movie> addMovie(
            @Valid @RequestBody MovieRequest movieRequest,
            Authentication connectedUser
    ) {
       return ResponseEntity.ok(movieService.save(movieRequest, connectedUser));
    }

    @GetMapping("{movie-id}")
    public ResponseEntity<MovieResponse> findMovieById(@PathVariable("movie-id") long movieId) {
        return ResponseEntity.ok(movieService.findById(movieId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MovieResponse>> findAllMovies(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(movieService.findAllMovies(page, size));
    }
}
