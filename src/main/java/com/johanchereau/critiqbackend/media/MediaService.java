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

    public MediaResponse save(MediaRequest mediaRequest) {
        MediaType mediaType = mediaTypeRepository.findById(mediaRequest.mediaTypeId())
                .orElseThrow(() -> new EntityNotFoundException("MediaType not found"));
        Movie movie = movieRepository.findById(mediaRequest.movieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        Media media = mediaMapper.toMedia(mediaRequest, mediaType, movie);
        media = mediaRepository.save(media);
        return mediaMapper.toMediaResponse(media);
    }

    public MediaResponse findById(long mediaId) {
        return mediaRepository.findById(mediaId)
                .map(mediaMapper::toMediaResponse)
                .orElseThrow(() -> new EntityNotFoundException("No media found with the id: " + mediaId));
    }

    public PageResponse<MediaResponse> findAllMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Media> mediaPage = mediaRepository.findAll(pageable);

        List<MediaResponse> mediaResponseList = mediaPage.stream()
                .map(mediaMapper::toMediaResponse)
                .toList();

        return new PageResponse<>(
                mediaResponseList,
                mediaPage.getNumber(),
                mediaPage.getSize(),
                mediaPage.getTotalElements(),
                mediaPage.getTotalPages(),
                mediaPage.isFirst(),
                mediaPage.isLast()
        );
    }
}
