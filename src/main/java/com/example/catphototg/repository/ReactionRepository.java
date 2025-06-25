package com.example.catphototg.repository;

import com.example.catphototg.entity.Cat;
import com.example.catphototg.entity.Reaction;
import com.example.catphototg.entity.User;
import com.example.catphototg.entity.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndCat(User user, Cat cat);
    boolean existsByUserAndCatAndType(User user, Cat cat, ReactionType type);
    int countByCatAndType(Cat cat, ReactionType type);
}
