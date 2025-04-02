package com.johanchereau.critiqbackend.favorite;

import com.johanchereau.critiqbackend.common.BaseEntity;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.user.User;
import jakarta.persistence.*;
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
@Table(
        name = "favorite",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
