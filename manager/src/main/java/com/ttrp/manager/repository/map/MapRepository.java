package com.ttrp.manager.repository.map;
import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.map.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MapRepository extends JpaRepository<Map, Long> {

    List<Map> getMapsByOwner(User owner);
}
