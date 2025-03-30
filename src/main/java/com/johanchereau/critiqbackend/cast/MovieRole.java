package com.johanchereau.critiqbackend.cast;

import com.johanchereau.critiqbackend.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MovieRole extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "movieRole")
    private List<MovieCast> casts;
}
