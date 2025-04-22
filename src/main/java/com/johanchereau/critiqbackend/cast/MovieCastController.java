package com.johanchereau.critiqbackend.cast;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movies/{movieId}/casts")
@RequiredArgsConstructor
@Tag(name = "MovieCast")
public class MovieCastController {

    private final MovieCastService movieCastService;

    @PostMapping
    public ResponseEntity<MovieCastResponse> addMovieCast(
            @PathVariable Long movieId,
            @Valid @RequestBody MovieCastRequest movieCastRequest
    ) {
        return ResponseEntity.ok(movieCastService.save(movieId, movieCastRequest));
    }

    @GetMapping
    public ResponseEntity<List<MovieCastResponse>> findAllByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieCastService.findAllByMovieId(movieId));
    }

    @GetMapping("{castId}")
    public ResponseEntity<MovieCastResponse> findMovieCastById(
            @PathVariable("movieId") Long movieId,
            @PathVariable("castId") Long castId
    ) {
        return ResponseEntity.ok(movieCastService.findByIdForMovie(movieId, castId));
    }
}
