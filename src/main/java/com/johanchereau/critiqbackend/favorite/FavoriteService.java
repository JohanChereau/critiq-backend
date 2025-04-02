package com.johanchereau.critiqbackend.favorite;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import com.johanchereau.critiqbackend.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final FavoriteMapper favoriteMapper;

    public PageResponse<FavoriteResponse> findUserFavorites(@PathVariable Long userId, int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(userId)) {
            throw new SecurityException("You can only access your own favorites.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Favorite> favorites = favoriteRepository.findByUserId(user.getId(), pageable);

        List<FavoriteResponse> favoriteResponses = favorites.stream()
                .map(favoriteMapper::toDto)
                .toList();

        return new PageResponse<>(
                favoriteResponses,
                favorites.getNumber(),
                favorites.getSize(),
                favorites.getTotalElements(),
                favorites.getTotalPages(),
                favorites.isFirst(),
                favorites.isLast()
        );
    }

    public FavoriteResponse addFavorite(Long userId, Long movieId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(userId)) {
            throw new SecurityException("You can only add favorites to your own account.");
        }

        if (favoriteRepository.findByUserIdAndMovieId(user.getId(), movieId).isPresent()) {
            throw new IllegalStateException("Movie is already in favorites.");
        }

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("No movie found with the id: " + movieId));

        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        return favoriteMapper.toDto(savedFavorite);
    }

    public FavoriteResponse removeFavorite(Long userId, Long movieId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(userId)) {
            throw new SecurityException("You can only remove favorites from your own account.");
        }

        Favorite favorite = favoriteRepository.findByUserIdAndMovieId(user.getId(), movieId)
                .orElseThrow(() -> new EntityNotFoundException("No favorite found for this movie and user."));

        favoriteRepository.delete(favorite);

        return favoriteMapper.toDto(favorite);
    }
}