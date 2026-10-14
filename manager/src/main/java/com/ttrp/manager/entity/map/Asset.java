package com.ttrp.manager.entity.map;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "asset_path", nullable = false)
    private String assetPath;

    @Column(nullable = false)
    private String category;

    @Builder.Default
    @Column(nullable = false)
    private String owner = "public";

}