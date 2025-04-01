package com.johanchereau.critiqbackend.cast;

import org.springframework.stereotype.Service;

@Service
public class PersonMapper {

    public Person toPerson(PersonRequest request) {
        return Person.builder()
                .name(request.name())
                .roles(request.roles())
                .photoUrl(request.photoUrl())
                .bio(request.bio())
                .dateOfBirth(request.dateOfBirth())
                .build();
    }

    public PersonResponse toPersonResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getRoles(),
                person.getPhotoUrl(),
                person.getBio(),
                person.getDateOfBirth()
        );
    }
}
