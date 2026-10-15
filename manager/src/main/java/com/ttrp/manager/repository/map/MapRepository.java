package com.ttrp.manager.repository.map;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.map.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MapRepository extends JpaRepository<Map, Long> {

    List<Map> getMapsByOwner(User owner);

    Optional<Map> findByNameAndOwnerId(String name, Long ownerId);

}


