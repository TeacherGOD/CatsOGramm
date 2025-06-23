package com.example.catphototg.repository;

import com.example.catphototg.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatRepository extends JpaRepository<Cat,Long> {
    List<Cat> findByAuthorId(Long authorId);
}
