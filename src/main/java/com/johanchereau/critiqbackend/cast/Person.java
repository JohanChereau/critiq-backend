package com.johanchereau.critiqbackend.cast;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.johanchereau.critiqbackend.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person extends BaseEntity {

    private String name;
    private String roles;
    private String photoUrl;
    private String bio;
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "person")
    @JsonManagedReference
    private List<MovieCast> casts;
}
