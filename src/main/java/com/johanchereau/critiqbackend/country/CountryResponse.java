package com.johanchereau.critiqbackend.country;

public record CountryResponse(
        Long id,
        String name,
        String isoCode
) {}
