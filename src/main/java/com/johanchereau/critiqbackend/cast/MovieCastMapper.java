package com.johanchereau.critiqbackend.cast;

import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieCastMapper {

    private final MovieRepository movieRepository;
    private final MovieRoleRepository movieRoleRepository;
    private final PersonRepository personRepository;

    public MovieCastMapper(MovieRepository movieRepository, MovieRoleRepository movieRoleRepository, PersonRepository personRepository) {
        this.movieRepository = movieRepository;
        this.movieRoleRepository = movieRoleRepository;
        this.personRepository = personRepository;
    }

    public MovieCast toMovieCast(MovieCastRequest movieCastRequest) {
        Movie movie = movieRepository.findById(movieCastRequest.movieId())
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + movieCastRequest.movieId()));
        MovieRole movieRole = movieRoleRepository.findById(movieCastRequest.movieRoleId())
                .orElseThrow(() -> new IllegalArgumentException("MovieRole not found with id: " + movieCastRequest.movieRoleId()));
        Person person = personRepository.findById(movieCastRequest.personId())
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + movieCastRequest.personId()));

        return MovieCast.builder()
                .movie(movie)
                .movieRole(movieRole)
                .person(person)
                .characterName(movieCastRequest.characterName())
                .build();
    }

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
