package com.johanchereau.critiqbackend.genre;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("genres")
@RequiredArgsConstructor
@Tag(name = "Genre")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreResponse> addGenre(@Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.save(genreRequest));
    }

    @GetMapping("{genre-id}")
    public ResponseEntity<GenreResponse> findGenreById(@PathVariable("genre-id") long genreId) {
        return ResponseEntity.ok(genreService.findById(genreId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<GenreResponse>> findAllGenres(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(genreService.findAllGenres(page, size));
    }
}
