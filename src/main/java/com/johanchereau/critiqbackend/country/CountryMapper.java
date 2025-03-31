package com.johanchereau.critiqbackend.country;

import org.springframework.stereotype.Service;

@Service
public class CountryMapper {

    public Country toCountry(CountryRequest countryRequest) {
        return Country.builder()
                .id(countryRequest.id())
                .name(countryRequest.name())
                .isoCode(countryRequest.isoCode())
                .build();
    }

    public CountryResponse toCountryResponse(Country country) {
        return new CountryResponse(country.getId(), country.getName(), country.getIsoCode());
    }
}
