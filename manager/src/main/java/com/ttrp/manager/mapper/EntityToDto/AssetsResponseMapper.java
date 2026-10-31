package com.ttrp.manager.mapper.EntityToDto;

import com.ttrp.manager.dto.map.response.AssetResponse;
import com.ttrp.manager.entity.map.Asset;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AssetsResponseMapper {
    public List<AssetResponse> toAssetsReponse(List<Asset> assets){
        List<AssetResponse> assetResponses = new ArrayList<>();
        for (Asset asset : assets){
            AssetResponse assetResponse = new AssetResponse(asset.getId(), asset.getName());
            assetResponses.add(assetResponse);
        }
        return assetResponses;
    }
}
