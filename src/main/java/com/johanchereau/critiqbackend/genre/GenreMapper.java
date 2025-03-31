package com.johanchereau.critiqbackend.genre;

import org.springframework.stereotype.Service;

@Service
public class GenreMapper {

    public Genre toGenre(GenreRequest genreRequest) {
        return Genre.builder()
                .name(genreRequest.name())
                .build();
    }

    public GenreResponse toGenreResponse(Genre genre) {
        return new GenreResponse(genre.getId(), genre.getName());
    }
}
