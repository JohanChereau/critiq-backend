package com.johanchereau.critiqbackend.genre;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.johanchereau.critiqbackend.common.BaseEntity;
import com.johanchereau.critiqbackend.movie.Movie;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Genre extends BaseEntity {

    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies;
}