package com.johanchereau.critiqbackend.cast;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("persons")
@RequiredArgsConstructor
@Tag(name = "Person")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonResponse> addPerson(@Valid @RequestBody PersonRequest personRequest) {
        return ResponseEntity.ok(personService.save(personRequest));
    }

    @GetMapping("{person-id}")
    public ResponseEntity<PersonResponse> findPersonById(@PathVariable("person-id") long personId) {
        return ResponseEntity.ok(personService.findById(personId));
    }

    @GetMapping
    public ResponseEntity<List<PersonResponse>> findAllPersons(
            @RequestParam(name = "name", required = false) String name
    ) {
        return ResponseEntity.ok(personService.findAllPersons(name));
    }
}
