package com.ttrp.manager.repository;

import com.ttrp.manager.entity.FriendRequest;
import com.ttrp.manager.entity.type.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver " +
            "WHERE fr.id = :id AND fr.receiver.id = :receiverId AND fr.status = :status")
    Optional<FriendRequest> findByIdAndReceiverIdAndStatus(
            @Param("id") Long id,
            @Param("receiverId") Long receiverId,
            @Param("status") FriendRequestStatus status
    );

    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver " +
            "WHERE fr.id = :id AND fr.sender.id = :senderId AND fr.status = :status")
    Optional<FriendRequest> findByIdAndSenderIdAndStatus(
            @Param("id") Long id,
            @Param("senderId") Long senderId,
            @Param("status") FriendRequestStatus status
    );

    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver " +
            "WHERE fr.receiver.id = :receiverId AND fr.status = :status")
    List<FriendRequest> findByReceiverIdAndStatus(
            @Param("receiverId") Long receiverId,
            @Param("status") FriendRequestStatus status
    );

    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver " +
            "WHERE fr.sender.id = :senderId AND fr.status = :status")
    List<FriendRequest> findBySenderIdAndStatus(
            @Param("senderId") Long senderId,
            @Param("status") FriendRequestStatus status
    );

    @Query("SELECT COUNT(fr) > 0 FROM FriendRequest fr WHERE fr.status = 'PENDING' AND " +
            "((fr.sender.id = :userA AND fr.receiver.id = :userB) OR (fr.sender.id = :userB AND fr.receiver.id = :userA))")
    boolean existsPendingBetween(@Param("userA") Long userA, @Param("userB") Long userB);
}
