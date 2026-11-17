package com.ttrp.manager.entity;

import com.ttrp.manager.entity.type.GameRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_members", uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameRole role;
}
