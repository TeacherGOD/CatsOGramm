package com.example.catphototg.repository;

import com.example.catphototg.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserIdAndCatId(Long userId, Long catId);
}
