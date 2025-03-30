package com.johanchereau.critiqbackend.cast;

import com.johanchereau.critiqbackend.common.BaseEntity;
import com.johanchereau.critiqbackend.movie.Movie;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieCast extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "movie_role_id", nullable = false)
    private MovieRole movieRole;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    private String characterName;
}
