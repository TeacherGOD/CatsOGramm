package com.example.catphototg.repository;

import com.example.catphototg.entity.Cat;
import com.example.common.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CatRepository extends JpaRepository<Cat,Long> {

    @Query(value = "SELECT * FROM cats c " +
            "WHERE NOT EXISTS (SELECT 1 FROM reactions r WHERE r.user_id = :userId AND r.cat_id = c.id) " +
            "ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Cat> findRandomUnseenCat(@Param("userId") Long userId);

    int countByAuthorId(Long userId);

    Page<Cat> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT COUNT(r) > 0 FROM Reaction r WHERE r.cat.id = :catId AND r.userId = :userId AND r.type = :type")
    boolean existsByUserAndCatAndType(@Param("userId") Long userId,
                                      @Param("catId") Long catId,
                                      @Param("type") ReactionType type);

}
