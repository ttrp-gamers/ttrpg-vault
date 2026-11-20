package com.ttrp.manager.entity;

import com.ttrp.manager.helper.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "monsters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    private String version = "2014";

    private String source;
    private String monsterType;
    private String challengeRating;
    private Integer maxHp;
    private Integer currentHp;

    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, Object> statblock = new HashMap<>();

    private Instant createdAt;
    private Instant updatedAt;
}
