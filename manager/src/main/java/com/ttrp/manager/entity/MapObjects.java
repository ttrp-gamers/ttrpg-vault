package com.ttrp.manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "map_objects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapObjects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long mapId;
    private String assetPath;
    private Integer gridX;
    private Integer gridY;
    private Integer width;
    private Integer height;
}
