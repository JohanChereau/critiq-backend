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

    public MovieCast save(MovieCastRequest movieCastRequest) {
        Movie movie = movieRepository.findById(movieCastRequest.movieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieCastRequest.movieId()));
        MovieRole movieRole = movieRoleRepository.findById(movieCastRequest.movieRoleId())
                .orElseThrow(() -> new EntityNotFoundException("MovieRole not found with id: " + movieCastRequest.movieRoleId()));
        Person person = personRepository.findById(movieCastRequest.personId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + movieCastRequest.personId()));

        MovieCast movieCast = movieCastMapper.toMovieCast(movieCastRequest);
        return movieCastRepository.save(movieCast);
    }

    public MovieCastResponse findById(long movieCastId) {
        return movieCastRepository.findById(movieCastId)
                .map(movieCastMapper::toMovieCastResponse)
                .orElseThrow(() -> new EntityNotFoundException("No movie cast found with id: " + movieCastId));
    }

    public List<MovieCastResponse> findAllByMovieId(long movieId) {
        List<MovieCast> movieCasts = movieCastRepository.findByMovieId(movieId);
        return movieCasts.stream()
                .map(movieCastMapper::toMovieCastResponse)
                .toList();
    }

    public List<MovieCastResponse> findAllByPersonId(long personId) {
        List<MovieCast> movieCasts = movieCastRepository.findByPersonId(personId);
        return movieCasts.stream()
                .map(movieCastMapper::toMovieCastResponse)
                .toList();
    }

    public List<MovieCastResponse> findAll() {
        List<MovieCast> movieCasts = movieCastRepository.findAll();
        return movieCasts.stream()
                .map(movieCastMapper::toMovieCastResponse)
                .toList();
    }
}
