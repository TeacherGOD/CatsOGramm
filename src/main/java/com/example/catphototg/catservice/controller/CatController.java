package com.example.catphototg.catservice.controller;

import com.example.catphototg.catservice.dto.CatCreationDto;
import com.example.catphototg.catservice.dto.CatDto;
import com.example.catphototg.catservice.service.CatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cats")
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    @PostMapping
    public CatDto addCat(@RequestBody CatCreationDto dto) {
        return catService.saveCat(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCat(@PathVariable Long id, @RequestParam Long userId) {
        catService.deleteCatById(id, userId);
    }


    // Другие методы по необходимости
}
