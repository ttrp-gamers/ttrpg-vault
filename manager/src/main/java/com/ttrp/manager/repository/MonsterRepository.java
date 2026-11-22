package com.ttrp.manager.repository;

import com.ttrp.manager.entity.Monster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonsterRepository extends JpaRepository<Monster, Long> {
    List<Monster> findAllByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<Monster> findByIdAndUserId(Long id, Long userId);
}
