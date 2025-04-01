package com.johanchereau.critiqbackend.cast;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
  List<MovieCast> findByMovieId(long movieId);

  List<MovieCast> findByPersonId(long personId);
}