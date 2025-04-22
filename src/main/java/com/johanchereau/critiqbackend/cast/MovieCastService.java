package com.johanchereau.critiqbackend.cast;

import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieCastService {

    private final MovieCastRepository movieCastRepository;
    private final MovieCastMapper movieCastMapper;
    private final MovieRepository movieRepository;
    private final MovieRoleRepository movieRoleRepository;
    private final PersonRepository personRepository;

    public MovieCastResponse save(Long movieId, MovieCastRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));

        var movieRole = movieRoleRepository.findById(request.movieRoleId())
                .orElseThrow(() -> new EntityNotFoundException("MovieRole not found with id: " + request.movieRoleId()));

        var person = personRepository.findById(request.personId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + request.personId()));

        var movieCast = MovieCast.builder()
                .movie(movie)
                .movieRole(movieRole)
                .person(person)
                .characterName(request.characterName())
                .build();

        return movieCastMapper.toMovieCastResponse(movieCastRepository.save(movieCast));
    }

    public MovieCastResponse findByIdForMovie(Long movieId, Long castId) {
        var cast = movieCastRepository.findById(castId)
                .filter(mc -> mc.getMovie().getId().equals(movieId))
                .orElseThrow(() -> new EntityNotFoundException("No cast with id " + castId + " found for movie " + movieId));

        return movieCastMapper.toMovieCastResponse(cast);
    }

    public List<MovieCastResponse> findAllByMovieId(Long movieId) {
        List<MovieCast> movieCasts = movieCastRepository.findByMovieId(movieId);
        return movieCasts.stream()
                .map(movieCastMapper::toMovieCastResponse)
                .toList();
    }
}
