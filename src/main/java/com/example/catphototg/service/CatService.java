package com.example.catphototg.service;

import com.example.catphototg.dto.CatCreationDto;
import com.example.catphototg.entity.Cat;
import com.example.catphototg.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatService {
    private final CatRepository catRepository;

    @Transactional
    public Cat saveCat(CatCreationDto dto) {
        Cat cat = new Cat();
        cat.setName(dto.name());
        cat.setFilepath(dto.filepath());
        cat.setAuthor(dto.author());
        return catRepository.save(cat);
    }
}

