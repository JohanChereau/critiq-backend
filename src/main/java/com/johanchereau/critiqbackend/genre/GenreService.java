package com.johanchereau.critiqbackend.genre;

import com.johanchereau.critiqbackend.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreResponse save(GenreRequest genreRequest) {
        Genre genre = genreMapper.toGenre(genreRequest);
        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }

    public GenreResponse findById(Long id) {
        return genreRepository.findById(id)
                .map(genreMapper::toGenreResponse)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    public PageResponse<GenreResponse> findAllGenres(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Genre> genres = genreRepository.findAll(pageable);
        List<GenreResponse> genreResponseList = genres.stream()
                .map(genreMapper::toGenreResponse)
                .toList();

        return new PageResponse<>(
                genreResponseList,
                genres.getNumber(),
                genres.getSize(),
                genres.getTotalElements(),
                genres.getTotalPages(),
                genres.isFirst(),
                genres.isLast()
        );
    }
}
