package com.johanchereau.critiqbackend.country;

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
public class Country extends BaseEntity {

    private String name;
    private String isoCode;

    @ManyToMany(mappedBy = "countries")
    private Set<Movie> movies;
}
