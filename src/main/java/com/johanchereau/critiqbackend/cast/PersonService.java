package com.johanchereau.critiqbackend.cast;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonResponse save(PersonRequest personRequest) {
        Person person = personMapper.toPerson(personRequest);
        return personMapper.toPersonResponse(personRepository.save(person));
    }

    public PersonResponse findById(Long personId) {
        return personRepository.findById(personId)
                .map(personMapper::toPersonResponse)
                .orElseThrow(() -> new EntityNotFoundException("No person found with id: " + personId));
    }

    public List<PersonResponse> findAllPersons(String nameFilter) {
        List<Person> persons;

        if (nameFilter != null && nameFilter.length() >= 3) {
            persons = personRepository.findByNameContainingIgnoreCase(nameFilter);
        } else {
            persons = personRepository.findAll();
        }

        return persons.stream()
                .map(personMapper::toPersonResponse)
                .collect(Collectors.toList());
    }
}
