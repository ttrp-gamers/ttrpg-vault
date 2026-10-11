package com.ttrp.manager.repository.map;

import com.ttrp.manager.entity.map.MapObject;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MapObjectRepository extends JpaRepository<MapObject, Long> {

    List<MapObject> findByMapNameAndMapOwnerId(String mapName, Long ownerId);
    void deleteByMapId(Long mapId);
}
