package com.ttrp.manager.repository;

import com.ttrp.manager.entity.MapObjects;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MapRepository extends JpaRepository<MapObjects, Long> {

    List<MapObjects> getFirstByMapId(Long mapId);

    List<MapObjects> findDistinctFirstByMapId(Long mapId);

    ObservationFilter findFirstByMapId(Long mapId);
}
