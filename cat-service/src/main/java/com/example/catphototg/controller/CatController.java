package com.example.catphototg.controller;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.exceptions.CatNotFoundException;
import com.example.catphototg.mapper.CatMapper;
import com.example.catphototg.service.CatService;
import com.example.catphototg.service.RandomCatService;
import com.example.common.dto.CatCreationDto;
import com.example.common.dto.CatDto;
import com.example.common.dto.CatWithoutAuthorNameDto;
import com.example.common.dto.PagedResponse;
import com.example.common.enums.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;
    private final CatMapper catMapper;
    private final RandomCatService randomCatService;

    @PostMapping
    public CatDto addCat(@RequestBody CatCreationDto dto) {
        return catService.saveCat(dto);
    }


    @GetMapping("/count")
    public int getCatsCount(@RequestParam Long userId) {
        return catService.getCatsCountByAuthor(userId);
    }

    @GetMapping
    public PagedResponse<CatDto> getCatsByAuthor(
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<CatDto> pageResult =catService.getCatsByAuthor(userId, page, size)
                .map(cat -> catMapper.toDto(cat, username));
        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements()
        );
    }

    @GetMapping("/{id}")
    public CatDto getCatById(
            @PathVariable Long id,
            @RequestParam String username
    ) throws CatNotFoundException {
        Cat cat = catService.getCatById(id);
        return catMapper.toDto(cat,  username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCat(@PathVariable Long id, @RequestParam Long userId) {
        boolean deleted = catService.deleteCatById(id, userId);
        return ResponseEntity.ok(deleted);
    }

    @GetMapping("/random")
    public CatWithoutAuthorNameDto getRandomCatForUserId(
            @RequestParam Long userId
    ) {
        return randomCatService.getRandomCatForUserId(userId)
                .orElseThrow(() -> new CatNotFoundException("No unseen cats available"));
    }

    @PostMapping("/{catId}/reaction")
    public ResponseEntity<Void> handleReaction(
            @PathVariable Long catId,
            @RequestParam Long userId,
            @RequestParam ReactionType type
    ) {
        randomCatService.processReaction(userId, catId, type);
        return ResponseEntity.ok().build();
    }
}
