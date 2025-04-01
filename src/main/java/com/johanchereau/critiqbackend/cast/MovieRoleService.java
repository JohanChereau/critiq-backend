package com.johanchereau.critiqbackend.cast;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieRoleService {

    private final MovieRoleRepository movieRoleRepository;
    private final MovieRoleMapper movieRoleMapper;

    public MovieRoleResponse save(MovieRoleRequest movieRoleRequest) {
        MovieRole movieRole = movieRoleMapper.toMovieRole(movieRoleRequest);
        return movieRoleMapper.toMovieRoleResponse(movieRoleRepository.save(movieRole));
    }

    public MovieRoleResponse findById(long movieRoleId) {
        return movieRoleRepository.findById(movieRoleId)
                .map(movieRoleMapper::toMovieRoleResponse)
                .orElseThrow(() -> new EntityNotFoundException("No movie role found with id: " + movieRoleId));
    }

    public List<MovieRoleResponse> findAll() {
        return movieRoleRepository.findAll()
                .stream()
                .map(movieRoleMapper::toMovieRoleResponse)
                .toList();
    }
}
