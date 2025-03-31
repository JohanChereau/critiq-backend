package com.johanchereau.critiqbackend.media;

import com.johanchereau.critiqbackend.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaTypeService {

    private final MediaTypeRepository mediaTypeRepository;
    private final MediaTypeMapper mediaTypeMapper;

    public MediaTypeResponse save(MediaTypeRequest mediaTypeRequest) {
        MediaType mediaType = mediaTypeMapper.toMediaType(mediaTypeRequest);
        return mediaTypeMapper.toMediaTypeResponse(mediaTypeRepository.save(mediaType));
    }

    public MediaTypeResponse findById(Long mediaTypeId) {
        MediaType mediaType = mediaTypeRepository.findById(mediaTypeId)
                .orElseThrow(() -> new RuntimeException("MediaType not found"));
        return mediaTypeMapper.toMediaTypeResponse(mediaType);
    }

    public PageResponse<MediaTypeResponse> findAllMediaTypes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("type").ascending());

        var mediaTypes = mediaTypeRepository.findAll(pageable);
        List<MediaTypeResponse> mediaTypeResponseList = mediaTypes.stream()
                .map(mediaTypeMapper::toMediaTypeResponse)
                .toList();

        return new PageResponse<>(
                mediaTypeResponseList,
                mediaTypes.getNumber(),
                mediaTypes.getSize(),
                mediaTypes.getTotalElements(),
                mediaTypes.getTotalPages(),
                mediaTypes.isFirst(),
                mediaTypes.isLast()
        );
    }
}
