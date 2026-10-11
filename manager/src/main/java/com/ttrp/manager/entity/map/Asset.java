package com.ttrp.manager.entity.map;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asset")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String owner = "public";
    @Column(name = "asset_path", nullable = false)
    private String assetPath;
}

