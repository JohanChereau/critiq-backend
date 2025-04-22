package com.johanchereau.critiqbackend.cast;

import org.springframework.stereotype.Service;

@Service
public class MovieCastMapper {

    public MovieCastResponse toMovieCastResponse(MovieCast movieCast) {
        return new MovieCastResponse(
                movieCast.getId(),
                movieCast.getCharacterName(),
                movieCast.getMovie().getId(),
                movieCast.getMovie().getTitle(),
                movieCast.getMovieRole().getId(),
                movieCast.getMovieRole().getName(),
                movieCast.getPerson().getId(),
                movieCast.getPerson().getName()
        );
    }
}
