package com.johanchereau.critiqbackend.favorite;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteMapper {

    public FavoriteResponse toDto(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getMovie().getId(),
                favorite.getMovie().getTitle(),
                favorite.getMovie().getPosterUrl()
        );
    }

    public List<FavoriteResponse> toDtoList(List<Favorite> favorites) {
        return favorites.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
