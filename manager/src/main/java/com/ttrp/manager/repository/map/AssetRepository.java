package com.ttrp.manager.repository.map;

import com.ttrp.manager.entity.map.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository  extends JpaRepository<Asset, Long> {
}
