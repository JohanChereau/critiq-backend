package com.johanchereau.critiqbackend.user;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "UserReview")
public class UserReviewController {

    private final UserReviewService userReviewService;

    @GetMapping("movies/{movieId}/reviews")
    public ResponseEntity<PageResponse<UserReviewResponse>> findReviewsByMovie(
            @PathVariable Long movieId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userReviewService.findReviewsByMovie(movieId, page, size));
    }

    @GetMapping("movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<UserReviewResponse> findReviewById(
            @PathVariable Long movieId,
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(userReviewService.findReviewByIdForMovie(movieId, reviewId));
    }

    @GetMapping("reviews/{reviewId}")
    public ResponseEntity<UserReviewResponse> findReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(userReviewService.findReviewById(reviewId));
    }

    @GetMapping("users/{userId}/reviews")
    public ResponseEntity<PageResponse<UserReviewResponse>> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(userReviewService.findReviewsByUser(userId, page, size, connectedUser));
    }

    @PostMapping("movies/{movieId}/reviews")
    public ResponseEntity<UserReviewResponse> addReview(
            @PathVariable Long movieId,
            @Valid @RequestBody UserReviewRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(userReviewService.saveReview(movieId, request, connectedUser));
    }

    @DeleteMapping("movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long movieId,
            @PathVariable Long reviewId,
            Authentication connectedUser
    ) {
        userReviewService.deleteReview(movieId, reviewId, connectedUser);
        return ResponseEntity.ok().build();
    }
}
