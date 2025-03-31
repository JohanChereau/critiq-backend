package com.johanchereau.critiqbackend.media;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("medias")
@RequiredArgsConstructor
@Tag(name = "Media")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<MediaResponse> addMedia(
            @Valid @RequestBody MediaRequest mediaRequest
    ) {
        return ResponseEntity.ok(mediaService.save(mediaRequest));
    }

    @GetMapping("{media-id}")
    public ResponseEntity<MediaResponse> findMediaById(@PathVariable("media-id") long mediaId) {
        return ResponseEntity.ok(mediaService.findById(mediaId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MediaResponse>> findAllMedia(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(mediaService.findAllMedia(page, size));
    }
}