package com.johanchereau.critiqbackend.media;

import org.springframework.stereotype.Service;

@Service
public class MediaTypeMapper {

    public MediaType toMediaType(MediaTypeRequest mediaTypeRequest) {
        return MediaType.builder()
                .type(mediaTypeRequest.type())
                .build();
    }

    public MediaTypeResponse toMediaTypeResponse(MediaType mediaType) {
        return new MediaTypeResponse(
                mediaType.getId(),
                mediaType.getType()
        );
    }
}
