package com.johanchereau.critiqbackend.movie;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.user.User;
import com.johanchereau.critiqbackend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    private User getConnectedUser(Authentication connectedUser) {
        String userEmail = connectedUser.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public MovieResponse findById(long movieId) {
        return movieRepository.findById(movieId)
                .map(movieMapper::toMovieResponse)
                .orElseThrow(() -> new EntityNotFoundException("No movie find with the id: " + movieId))
                ;
    }

    public PageResponse<MovieResponse> findAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Movie> movies = movieRepository.findAll(pageable);
        List<MovieResponse> movieResponseList = movies.stream()
                .map(movieMapper::toMovieResponse)
                .toList();

        return new PageResponse<>(
                movieResponseList,
                movies.getNumber(),
                movies.getSize(),
                movies.getTotalElements(),
                movies.getTotalPages(),
                movies.isFirst(),
                movies.isLast()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Movie save(MovieRequest movieRequest, Authentication connectedUser) {
        getConnectedUser(connectedUser);

        Movie movie = movieMapper.toMovie(movieRequest);
        return movieRepository.save(movie);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Movie update(long movieId, MovieRequest request, Authentication auth) {
        getConnectedUser(auth);
        Movie existing = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("No movie find with the id: " + movieId));

        existing.setTitle(request.title());
        existing.setDescription(request.description());
        existing.setStoryline(request.storyline());
        existing.setTagline(request.tagline());
        existing.setReleaseDate(request.releaseDate());
        existing.setBudgetEur(request.budgetEur());
        existing.setClassification(request.classification());
        existing.setDurationMinutes(request.durationMinutes());
        existing.setPosterUrl(request.posterUrl());
        existing.setBannerUrl(request.bannerUrl());

        return movieRepository.save(existing);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MovieDeleteResponse delete(long movieId, Authentication auth) {
        getConnectedUser(auth);

        Movie toDelete = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new EntityNotFoundException("No movie find with the id: " + movieId)
                );

        movieRepository.delete(toDelete);

        return new MovieDeleteResponse(movieId);
    }
}
