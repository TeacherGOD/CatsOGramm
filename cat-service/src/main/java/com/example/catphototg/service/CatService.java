package com.example.catphototg.service;


import com.example.catphototg.entity.Cat;
import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.mapper.CatMapper;
import com.example.catphototg.mapper.CatWithoutAuthorNameDtoMapper;
import com.example.catphototg.repository.CatRepository;
import com.example.common.dto.CatCreationDto;
import com.example.common.dto.CatDto;
import com.example.common.dto.CatWithoutAuthorNameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatService {
    private final CatRepository catRepository;
    private final FileStorageService fileStorageService;
    private final CatMapper catMapper;

    @Transactional
    public CatDto  saveCat(CatCreationDto dto) {
        Cat cat = new Cat();
        cat.setName(dto.name());
        cat.setFilePath(dto.filepath());
        cat.setAuthorId(dto.authorId());

        return catMapper.toDto(catRepository.save(cat),dto.authorName());
    }

    public int getCatsCountByAuthor(Long userId) {
        return catRepository.countByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Cat> getCatsByAuthor(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return catRepository.findByAuthorId(userId, pageable);
    }

    public Cat getCatById(Long catId) throws CatNotFoundException {
        return catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException("Кошка с идентификатором " + catId +" не найден"));
    }

    public boolean deleteCatById(Long catId, Long userId) {
        Optional<Cat> catOpt = catRepository.findById(catId);
        if (catOpt.isEmpty()) return false;

        Cat cat = catOpt.get();
        if (!cat.getAuthorId().equals(userId)) {
            return false;
        }

        fileStorageService.delete(cat.getFilePath());

        catRepository.delete(cat);
        return true;
    }

    public Optional<CatWithoutAuthorNameDto> findRandomUnseenCat(Long userId) {
        return catRepository.findRandomUnseenCat(userId)
                .map(CatWithoutAuthorNameDtoMapper::toDto);
    }
}

