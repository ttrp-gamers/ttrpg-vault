package com.ttrp.manager.repository;

import com.ttrp.manager.entity.GameMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameMemberRepository extends JpaRepository<GameMember, Long> {
    List<GameMember> findAllByUserId(Long userId);

    boolean existsByGameIdAndUserId(Long gameId, Long userId);

    Optional<GameMember> findByGameIdAndUserId(Long gameId, Long userId);
}
