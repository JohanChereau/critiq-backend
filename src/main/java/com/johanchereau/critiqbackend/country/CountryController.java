package com.johanchereau.critiqbackend.country;

import com.johanchereau.critiqbackend.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("countries")
@RequiredArgsConstructor
@Tag(name = "Country")
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    public ResponseEntity<CountryResponse> addCountry(@Valid @RequestBody CountryRequest countryRequest) {
        return ResponseEntity.ok(countryService.save(countryRequest));
    }

    @GetMapping("{country-id}")
    public ResponseEntity<CountryResponse> findCountryById(@PathVariable("country-id") long countryId) {
        return ResponseEntity.ok(countryService.findById(countryId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CountryResponse>> findAllCountries(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(countryService.findAllCountries(page, size));
    }
}
