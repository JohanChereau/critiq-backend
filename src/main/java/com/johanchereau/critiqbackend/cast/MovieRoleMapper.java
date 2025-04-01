package com.johanchereau.critiqbackend.cast;

import org.springframework.stereotype.Service;

@Service
public class MovieRoleMapper {

    public MovieRole toMovieRole(MovieRoleRequest request) {
        return MovieRole.builder()
                .name(request.name())
                .build();
    }

    public MovieRoleResponse toMovieRoleResponse(MovieRole movieRole) {
        return new MovieRoleResponse(
                movieRole.getId(),
                movieRole.getName()
        );
    }
}
