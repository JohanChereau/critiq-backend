package com.johanchereau.critiqbackend.media;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movies/{movieId}/medias")
@RequiredArgsConstructor
@Tag(name = "Media")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<MediaResponse> addMedia(
            @PathVariable Long movieId,
            @Valid @RequestBody MediaRequest mediaRequest
    ) {
        return ResponseEntity.ok(mediaService.save(movieId, mediaRequest));
    }

    @GetMapping
    public ResponseEntity<List<MediaResponse>> findAllMediaForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(mediaService.findAllForMovie(movieId));
    }

    @GetMapping("{mediaId}")
    public ResponseEntity<MediaResponse> findByIdForMovie(
            @PathVariable Long movieId,
            @PathVariable Long mediaId
    ) {
        return ResponseEntity.ok(mediaService.findByIdForMovie(movieId, mediaId));
    }
}