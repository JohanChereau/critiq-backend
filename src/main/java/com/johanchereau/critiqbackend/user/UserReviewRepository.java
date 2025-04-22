package com.johanchereau.critiqbackend.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    Page<UserReview> findByMovieId(Long movieId, Pageable pageable);
    Page<UserReview> findByUserId(Long userId, Pageable pageable);
    Optional<UserReview> findByUserIdAndMovieId(Long userId, Long movieId);
}
