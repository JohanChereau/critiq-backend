package com.johanchereau.critiqbackend.language;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Language extends BaseEntity {

    private String name;
    private String isoCode;

    @OneToMany(mappedBy = "language")
    @JsonManagedReference
    private List<MovieLanguage> movieLanguages;
}
