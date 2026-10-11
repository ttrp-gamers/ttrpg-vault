package com.ttrp.manager.entity.map;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "map_objects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", nullable = false)
    private Map map;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    @Column(name = "grid_x", nullable = false)
    private Integer gridX;
    @Column(name = "grid_y", nullable = false)
    private Integer gridY;
    @Column(name = "width_in_cells", nullable = false)
    private Integer width;
    @Column(name = "height_in_cells", nullable = false)
    private Integer height;
}
