package com.example.catphototg.catservice.repository;

import com.example.catphototg.catservice.entity.Cat;
import com.example.catphototg.bot.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CatRepository extends JpaRepository<Cat,Long> {

    Page<Cat> findByAuthor(User author, PageRequest pageable);

    @Query("SELECT c FROM Cat c " +
            "JOIN FETCH c.author " + // Добавляем JOIN FETCH для загрузки автора
            "WHERE NOT EXISTS (SELECT 1 FROM Reaction r WHERE r.user.id = :userId AND r.cat.id = c.id) " +
            "ORDER BY RANDOM() " +
            "LIMIT 1")
    Optional<Cat> findRandomUnseenCat(@Param("userId") Long userId);
}
