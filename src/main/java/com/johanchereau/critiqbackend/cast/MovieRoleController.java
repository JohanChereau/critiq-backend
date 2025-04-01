package com.johanchereau.critiqbackend.cast;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movie-roles")
@RequiredArgsConstructor
@Tag(name = "MovieRole")
public class MovieRoleController {

    private final MovieRoleService movieRoleService;

    @PostMapping
    public ResponseEntity<MovieRoleResponse> addMovieRole(@Valid @RequestBody MovieRoleRequest movieRoleRequest) {
        return ResponseEntity.ok(movieRoleService.save(movieRoleRequest));
    }

    @GetMapping("{movie-role-id}")
    public ResponseEntity<MovieRoleResponse> findMovieRoleById(@PathVariable("movie-role-id") long movieRoleId) {
        return ResponseEntity.ok(movieRoleService.findById(movieRoleId));
    }

    @GetMapping
    public ResponseEntity<List<MovieRoleResponse>> findAllMovieRoles() {
        return ResponseEntity.ok(movieRoleService.findAll());
    }
}
