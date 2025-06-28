package com.example.catphototg.service;


import com.example.catphototg.entity.Cat;
import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.kafka.CatDto;
import com.example.catphototg.mapper.CatMapper;
import com.example.catphototg.mapper.CatWithoutAuthorNameDtoMapper;
import com.example.catphototg.repository.CatRepository;
import com.example.common.dto.CatCreationDto;
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
    private final CatMapper catMapper;

    @Transactional
    public CatDto saveCat(CatCreationDto dto) {
        Cat cat = new Cat();
        cat.setName(dto.name());
        cat.setFilePath(dto.filepath());
        cat.setAuthorId(dto.authorId());

        Cat savedCat = catRepository.save(cat);

        return catMapper.toDto(cat,dto.authorName(),null);
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

    public String deleteCatById(Long catId, Long userId) {
        Optional<Cat> catOpt = catRepository.findById(catId);
        if (catOpt.isEmpty()) throw new CatNotFoundException("Cat not found with id: "+catId);

        Cat cat = catOpt.get();
        var filepath=cat.getFilePath();
        if (!cat.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException(String.format("Not your cat(%d) user with id (%d)",catId,userId));
        }

        //fileStorageService.delete(cat.getFilePath());

        catRepository.delete(cat);
        return filepath;
    }

    public Optional<CatWithoutAuthorNameDto> findRandomUnseenCat(Long userId) {
        return catRepository.findRandomUnseenCat(userId)
                .map(CatWithoutAuthorNameDtoMapper::toDto);
    }
}

