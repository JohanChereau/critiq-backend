package com.johanchereau.critiqbackend.favorite;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users/{userId}/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<PageResponse<FavoriteResponse>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(favoriteService.findUserFavorites(userId, page, size, connectedUser));
    }

    @PostMapping("{movieId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @PathVariable Long userId,
            @PathVariable Long movieId,
            Authentication connectedUser
    ) {
        FavoriteResponse response = favoriteService.addFavorite(userId, movieId, connectedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{movieId}")
    public ResponseEntity<FavoriteResponse> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long movieId,
            Authentication connectedUser
    ) {
        FavoriteResponse response = favoriteService.removeFavorite(userId, movieId, connectedUser);
        return ResponseEntity.ok(response);
    }
}
