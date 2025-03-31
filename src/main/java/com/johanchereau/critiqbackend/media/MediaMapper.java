package com.johanchereau.critiqbackend.media;

import com.johanchereau.critiqbackend.movie.Movie;
import org.springframework.stereotype.Service;

@Service
public class MediaMapper {

    public Media toMedia(MediaRequest mediaRequest, MediaType mediaType, Movie movie) {
        return Media.builder()
                .title(mediaRequest.title())
                .alt(mediaRequest.alt())
                .url(mediaRequest.url())
                .mediaType(mediaType)
                .movie(movie)
                .build();
    }

    public MediaResponse toMediaResponse(Media media) {
        return new MediaResponse(
                media.getId(),
                media.getTitle(),
                media.getAlt(),
                media.getUrl(),
                media.getMediaType().getId(),
                media.getMovie().getId()
        );
    }
}
