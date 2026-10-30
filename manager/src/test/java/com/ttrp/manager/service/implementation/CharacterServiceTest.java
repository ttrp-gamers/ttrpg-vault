package com.ttrp.manager.service.implementation;

import com.ttrp.manager.dto.character.CharacterRequest;
import com.ttrp.manager.dto.character.CharacterResponse;
import com.ttrp.manager.entity.Character;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.AccountStatus;
import com.ttrp.manager.entity.type.UserRole;
import com.ttrp.manager.mapper.CharacterMapper;
import com.ttrp.manager.service.declaration.IRulesetService;
import com.ttrp.manager.repository.CharacterRepository;
import com.ttrp.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IRulesetService rulesetService;

    private final CharacterMapper characterMapper = Mappers.getMapper(CharacterMapper.class);

    private CharacterService characterService;

    private User testUser;

    @BeforeEach
    void setUp() {
        characterService = new CharacterService(characterRepository, userRepository, characterMapper, rulesetService);

        testUser = User.builder()
                .id(1L)
                .username("adventurer")
                .email("adventurer@dnd.com")
                .password("secret123")
                .userRole(UserRole.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("Create Character Tests")
    class CreateCharacterTests {

        @Test
        @DisplayName("Successfully creates character with attributes and default current HP")
        void createsCharacterSuccessfully() {
            CharacterRequest request = new CharacterRequest(
                    "Conan",
                    "Barbarian",
                    1,
                    "D&D 5e",
                    15,
                    null,
                    Map.of("strength", 18, "constitution", 16)
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(characterRepository.save(any(Character.class))).thenAnswer(invocation -> {
                Character entity = invocation.getArgument(0);
                entity.setId(100L);
                return entity;
            });

            CharacterResponse response = characterService.createCharacter(1L, request);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(100L);
            assertThat(response.name()).isEqualTo("Conan");
            assertThat(response.characterClass()).isEqualTo("Barbarian");
            assertThat(response.maxHp()).isEqualTo(15);
            assertThat(response.currentHp()).isEqualTo(15); // defaulted to maxHp
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.attributes()).containsEntry("strength", 18);
        }

        @Test
        @DisplayName("Throws when user is not found")
        void throwsWhenUserNotFound() {
            CharacterRequest request = new CharacterRequest(
                    "Ghost",
                    "Rogue",
                    1,
                    "D&D 5e",
                    10,
                    10,
                    Map.of()
            );

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> characterService.createCharacter(99L, request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("Read Character Tests")
    class ReadCharacterTests {

        @Test
        @DisplayName("Returns list of characters for user")
        void returnsCharactersForUser() {
            Character char1 = Character.builder()
                    .id(10L)
                    .user(testUser)
                    .name("Legolas")
                    .characterClass("Ranger")
                    .level(3)
                    .ruleset("D&D 5e")
                    .maxHp(28)
                    .currentHp(28)
                    .attributes(Map.of("dexterity", 18))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(characterRepository.findAllByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(List.of(char1));

            List<CharacterResponse> list = characterService.getCharactersByUser(1L);

            assertThat(list).hasSize(1);
            assertThat(list.get(0).name()).isEqualTo("Legolas");
        }

        @Test
        @DisplayName("Returns single character by id")
        void returnsCharacterById() {
            Character char1 = Character.builder()
                    .id(10L)
                    .user(testUser)
                    .name("Gimli")
                    .characterClass("Fighter")
                    .level(3)
                    .ruleset("D&D 5e")
                    .maxHp(32)
                    .currentHp(30)
                    .attributes(Map.of("strength", 16))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(characterRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(char1));

            CharacterResponse response = characterService.getCharacterById(1L, 10L);

            assertThat(response.name()).isEqualTo("Gimli");
            assertThat(response.maxHp()).isEqualTo(32);
        }
    }

    @Nested
    @DisplayName("Update & Delete Character Tests")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Updates existing character")
        void updatesCharacterSuccessfully() {
            Character existing = Character.builder()
                    .id(10L)
                    .user(testUser)
                    .name("Gandalf")
                    .characterClass("Wizard")
                    .level(5)
                    .ruleset("D&D 5e")
                    .maxHp(25)
                    .currentHp(25)
                    .attributes(Map.of("intelligence", 18))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(characterRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(existing));
            when(characterRepository.save(any(Character.class))).thenAnswer(inv -> inv.getArgument(0));

            CharacterRequest updateRequest = new CharacterRequest(
                    "Gandalf the White",
                    "Wizard",
                    6,
                    "D&D 5e",
                    30,
                    28,
                    Map.of("intelligence", 20)
            );

            CharacterResponse response = characterService.updateCharacter(1L, 10L, updateRequest);

            assertThat(response.name()).isEqualTo("Gandalf the White");
            assertThat(response.level()).isEqualTo(6);
            assertThat(response.maxHp()).isEqualTo(30);
        }

        @Test
        @DisplayName("Deletes character when found")
        void deletesCharacterSuccessfully() {
            Character existing = Character.builder()
                    .id(10L)
                    .user(testUser)
                    .name("Boromir")
                    .build();

            when(characterRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(existing));

            characterService.deleteCharacter(1L, 10L);

            verify(characterRepository).delete(existing);
        }
    }
}
