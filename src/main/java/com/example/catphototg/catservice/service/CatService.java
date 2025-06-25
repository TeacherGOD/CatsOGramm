package com.example.catphototg.catservice.service;

import com.example.catphototg.bot.entity.User;
import com.example.catphototg.catservice.dto.CatCreationDto;
import com.example.catphototg.catservice.dto.CatDto;
import com.example.catphototg.catservice.entity.Cat;
import com.example.catphototg.catservice.exceptions.CatNotFoundException;
import com.example.catphototg.catservice.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatService {
    private final CatRepository catRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public CatDto  saveCat(CatCreationDto dto) {
        Cat cat = new Cat();
        cat.setName(dto.name());
        cat.setFilePath(dto.filepath());
        var author=new User();
        author.setId(dto.authorId());
        cat.setAuthor(author);

        Cat savedCat = catRepository.save(cat);

        return new CatDto(
                savedCat.getId(),
                savedCat.getName(),
                savedCat.getFilePath(),
                dto.authorName(),
                new HashMap<>(),
                new HashMap<>()
        );
    }

    public int getCatsCountByAuthor(Long userId) {
        return catRepository.countByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Cat> getCatsByAuthor(User author, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return catRepository.findByAuthor(author, pageable);
    }

    public Cat getCatById(Long catId) throws CatNotFoundException {
        return catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException("Кошка с идентификатором " + catId +" не найден"));
    }

    public boolean deleteCatById(Long catId, User user) {
        Optional<Cat> catOpt = catRepository.findById(catId);
        if (catOpt.isEmpty()) return false;

        Cat cat = catOpt.get();
        if (!cat.getAuthor().getId().equals(user.getId())) {
            return false;
        }

        fileStorageService.delete(cat.getFilePath());

        catRepository.delete(cat);
        return true;
    }


    public Optional<Cat> findRandomUnseenCat(Long userId) {
        return catRepository.findRandomUnseenCat(userId);
    }
}

