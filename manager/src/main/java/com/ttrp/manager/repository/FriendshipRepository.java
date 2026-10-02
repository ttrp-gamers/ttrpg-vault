package com.ttrp.manager.repository;

import com.ttrp.manager.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
            "(f.userA.id = :userA AND f.userB.id = :userB) OR (f.userA.id = :userB AND f.userB.id = :userA)")
    boolean existsBetween(@Param("userA") Long userA, @Param("userB") Long userB);
}
