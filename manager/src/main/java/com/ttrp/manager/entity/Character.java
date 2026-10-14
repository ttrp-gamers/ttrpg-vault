package com.ttrp.manager.entity;

import com.ttrp.manager.helper.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "characters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String characterClass;

    @Builder.Default
    private Integer level = 1;

    @Builder.Default
    private String ruleset = "D&D 5e";

    private Integer maxHp;

    private Integer currentHp;

    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    private Instant createdAt;

    private Instant updatedAt;
}

