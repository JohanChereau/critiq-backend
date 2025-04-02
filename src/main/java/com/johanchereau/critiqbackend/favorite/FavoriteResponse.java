package com.johanchereau.critiqbackend.favorite;

public record FavoriteResponse(Long id, Long movieId, String title, String posterUrl) {
}
