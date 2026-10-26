package com.ttrp.manager.repository;

import com.ttrp.manager.entity.Friendship;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FriendshipRepositoryTest {
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;

    List<User> generateUsers(int numberOfUsers) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < numberOfUsers; i++) {
            User user = User.builder()
                    .username("user" + i)
                    .email("test" + i + "@test.com")
                    .password("123")
                    .userRole(UserRole.USER)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            userRepository.save(user);
            users.add(user);
        }
        return users;
    }

    void generateFriendships(User user, List<User> friends) {
        for (User f : friends) {
            Friendship friendship = Friendship.builder()
                    .userA(user)
                    .userB(f)
                    .createdAt(Instant.now())
                    .build();

            friendshipRepository.save(friendship);
        }
    }

    @Test
    void getFriendshipReturnsFriendshipWhenItExists() {
        List<User> users = generateUsers(2);
        generateFriendships(users.get(0), users);
        assertThat(friendshipRepository.getFriendship(users.get(0).getId(), users.get(1).getId())).isPresent();
    }

    @Test
    void getAllFriendshipsByIdReturnsEveryFriendshipForUser() {
        List<User> users = generateUsers(5);
        User target = users.get(0);
        List<User> friends = users.stream().filter(u -> !u.getId().equals(target.getId())).toList();
        generateFriendships(target, friends);
        assertThat((long) friendshipRepository.getAllFriendshipsById(target.getId()).size()).isEqualTo(4);
    }

    @Test
    void getAllFriendshipsByIdReturnsEmptyListWhenUserHasNoFriends() {
        List<User> users = generateUsers(1);
        User target = users.get(0);

        assertThat(friendshipRepository.getAllFriendshipsById(target.getId())).isEmpty();
    }

    @Test
    void getAllFriendshipsByIdReturnsEmptyListForNonexistentUser() {
        assertThat(friendshipRepository.getAllFriendshipsById(-1L)).isEmpty();
    }
}
