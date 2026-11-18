package com.ttrp.manager.repository;

import com.ttrp.manager.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
