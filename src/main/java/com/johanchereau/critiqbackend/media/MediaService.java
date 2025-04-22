package com.johanchereau.critiqbackend.media;

import com.johanchereau.critiqbackend.common.PageResponse;
import com.johanchereau.critiqbackend.movie.Movie;
import com.johanchereau.critiqbackend.movie.MovieRepository;
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
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final MediaTypeRepository mediaTypeRepository;
    private final MovieRepository movieRepository;

    public MediaResponse save(Long movieId, MediaRequest request) {
        var mediaType = mediaTypeRepository.findById(request.mediaTypeId())
                .orElseThrow(() -> new EntityNotFoundException("MediaType not found"));

        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        var media = mediaMapper.toMedia(request, mediaType, movie);
        return mediaMapper.toMediaResponse(mediaRepository.save(media));
    }

    public MediaResponse findByIdForMovie(Long movieId, Long mediaId) {
        var media = mediaRepository.findById(mediaId)
                .filter(m -> m.getMovie().getId().equals(movieId))
                .orElseThrow(() -> new EntityNotFoundException("No media found with id " + mediaId + " for movie " + movieId));

        return mediaMapper.toMediaResponse(media);
    }

    public List<MediaResponse> findAllForMovie(Long movieId) {
        return mediaRepository.findAll().stream()
                .filter(media -> media.getMovie().getId().equals(movieId))
                .map(mediaMapper::toMediaResponse)
                .toList();
    }
}
