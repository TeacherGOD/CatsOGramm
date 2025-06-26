package com.example.catphototg.catservice.repository;

import com.example.catphototg.catservice.entity.Cat;
import com.example.catphototg.catservice.entity.Reaction;
import com.example.catphototg.bot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndCat(User user, Cat cat);
}
