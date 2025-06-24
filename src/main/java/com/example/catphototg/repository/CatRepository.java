package com.example.catphototg.repository;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatRepository extends JpaRepository<Cat,Long> {
    List<Cat> findByAuthorId(Long authorId);

    Page<Cat> findByAuthor(User author, PageRequest pageable);
}
