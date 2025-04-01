package com.johanchereau.critiqbackend.cast;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movie-casts")
@RequiredArgsConstructor
@Tag(name = "MovieCast")
public class MovieCastController {

    private final MovieCastService movieCastService;

    @PostMapping
    public ResponseEntity<MovieCast> addMovieCast(
            @Valid @RequestBody MovieCastRequest movieCastRequest
    ) {
        return ResponseEntity.ok(movieCastService.save(movieCastRequest));
    }

    @GetMapping("{movie-cast-id}")
    public ResponseEntity<MovieCastResponse> findMovieCastById(@PathVariable("movie-cast-id") long movieCastId) {
        return ResponseEntity.ok(movieCastService.findById(movieCastId));
    }

    @GetMapping("/movie/{movie-id}")
    public ResponseEntity<List<MovieCastResponse>> findByMovie(@PathVariable("movie-id") long movieId) {
        return ResponseEntity.ok(movieCastService.findAllByMovieId(movieId));
    }

    @GetMapping("/person/{person-id}")
    public ResponseEntity<List<MovieCastResponse>> findByPerson(@PathVariable("person-id") long personId) {
        return ResponseEntity.ok(movieCastService.findAllByPersonId(personId));
    }
}
