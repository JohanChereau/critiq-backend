package com.johanchereau.critiqbackend.media;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("media-types")
@RequiredArgsConstructor
@Tag(name = "MediaType")
public class MediaTypeController {

    private final MediaTypeService mediaTypeService;

    @PostMapping
    public ResponseEntity<MediaTypeResponse> addMediaType(@Valid @RequestBody MediaTypeRequest mediaTypeRequest) {
        return ResponseEntity.ok(mediaTypeService.save(mediaTypeRequest));
    }

    @GetMapping("{media-type-id}")
    public ResponseEntity<MediaTypeResponse> findMediaTypeById(@PathVariable("media-type-id") long mediaTypeId) {
        return ResponseEntity.ok(mediaTypeService.findById(mediaTypeId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MediaTypeResponse>> findAllMediaTypes(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(mediaTypeService.findAllMediaTypes(page, size));
    }
}
