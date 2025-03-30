package com.johanchereau.critiqbackend.media;

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
public class Media extends BaseEntity {

    private String title;
    private String alt;
    private String url;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private MediaType mediaType;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
}
