package com.ttrp.manager.entity.map;

import com.ttrp.manager.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "grid_size", nullable = false)
    private Integer gridSize = 40;

    @Builder.Default
    @Column(nullable = false)
    private Integer columns = 25;

    @Builder.Default
    @Column(nullable = false)
    private Integer rows = 25;

    @Column(name = "background_path")
    private String backgroundPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MapObject> objects = new ArrayList<>();
}
