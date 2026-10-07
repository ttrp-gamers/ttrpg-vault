package com.ttrp.manager.repository;

import com.ttrp.manager.entity.Friendship;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FriendshipRepositoryTest {
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void getFriendshipReturnsFriendshipWhenItExists() {
        User userA = User.builder()
                .username("sender")
                .email("sender@test.com")
                .password("encoded_password")
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User userB = User.builder()
                .username("receiver")
                .email("receiver@test.com")
                .password("encoded_password")
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User savedUserA = userRepository.save(userA);
        User savedUserB = userRepository.save(userB);

        Friendship friendship = Friendship.builder()
                .userA(savedUserA)
                .userB(savedUserB)
                .createdAt(Instant.now())
                .build();

        friendshipRepository.save(friendship);

        assertThat(friendshipRepository.getFriendship(savedUserA.getId(), savedUserB.getId())).isPresent();
    }
}
