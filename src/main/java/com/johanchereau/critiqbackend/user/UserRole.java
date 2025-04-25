package com.johanchereau.critiqbackend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.johanchereau.critiqbackend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRole extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "userRoles")
    @JsonIgnore
    private List<User> users;
}
