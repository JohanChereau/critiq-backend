package com.johanchereau.critiqbackend.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    Optional<Favorite> findByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);
}
