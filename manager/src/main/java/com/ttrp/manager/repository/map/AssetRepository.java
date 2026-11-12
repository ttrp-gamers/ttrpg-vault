package com.ttrp.manager.repository.map;

import com.ttrp.manager.entity.map.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository  extends JpaRepository<Asset, Long> {
    @Query("SELECT a FROM Asset a WHERE a.category = :category AND (a.owner IS NULL OR a.owner.id = :userId)")
    List<Asset> getAllByCategory(
            @Param("userId") Long userId,
            @Param("category") String category
    );

    Asset getAssetById(Long id);
}
