package com.johanchereau.critiqbackend.country;

import com.johanchereau.critiqbackend.common.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryResponse save(CountryRequest countryRequest) {
        Country country = countryMapper.toCountry(countryRequest);
        return countryMapper.toCountryResponse(countryRepository.save(country));
    }

    public CountryResponse findById(long countryId) {
        return countryRepository.findById(countryId)
                .map(countryMapper::toCountryResponse)
                .orElseThrow(() -> new EntityNotFoundException("No country found with id: " + countryId));
    }

    public PageResponse<CountryResponse> findAllCountries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Country> countries = countryRepository.findAll(pageable);
        List<CountryResponse> countryResponses = countries.stream()
                .map(countryMapper::toCountryResponse)
                .toList();

        return new PageResponse<>(
                countryResponses,
                countries.getNumber(),
                countries.getSize(),
                countries.getTotalElements(),
                countries.getTotalPages(),
                countries.isFirst(),
                countries.isLast()
        );
    }
}
