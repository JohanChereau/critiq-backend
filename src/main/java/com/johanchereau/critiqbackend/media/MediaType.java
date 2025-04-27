package com.johanchereau.critiqbackend.media;

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
public class MediaType extends BaseEntity {

    private String type;

    @OneToMany(mappedBy = "mediaType")
    @JsonManagedReference
    private List<Media> media;
}
