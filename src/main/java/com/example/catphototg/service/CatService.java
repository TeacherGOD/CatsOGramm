package com.example.catphototg.service;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.catphototg.entity.User;

@Service
@RequiredArgsConstructor
public class CatService {
    private final CatRepository catRepository;

    @Transactional
    public Cat saveCat(String name, String filepath, User author) {
        Cat cat = new Cat();
        cat.setName(name);
        cat.setFilePath(filepath);
        cat.setAuthor(author);
        return catRepository.save(cat);
    }
}

