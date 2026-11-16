package com.ttrp.manager.service.implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Open5eClient {
    private final ObjectMapper objectMapper;

    @Value("${app.open5e.base-url:https://api.open5e.com}")
    private String baseUrl;

    public List<Map<String, Object>> getClasses(String gamesystemKey) {
        return getClassEntries(gamesystemKey).stream()
                .filter(entry -> !entry.containsKey("subclass_of"))
                .toList();
    }

    public List<Map<String, Object>> getSubclasses(String gamesystemKey) {
        return getClassEntries(gamesystemKey).stream()
                .filter(entry -> entry.get("subclass_of") instanceof Map<?, ?>)
                .toList();
    }

    private List<Map<String, Object>> getClassEntries(String gamesystemKey) {
        return getResults("/v2/classes/").stream()
                .filter(entry -> {
                    if (!(entry.get("document") instanceof Map<?, ?> document)
                            || !(document.get("gamesystem") instanceof Map<?, ?> gamesystem)) {
                        return false;
                    }
                    return gamesystemKey.equals(gamesystem.get("key"));
                })
                .toList();
    }

    public List<Map<String, Object>> getSpecies() {
        return getResults("/v2/species/");
    }

    public List<Map<String, Object>> getBackgrounds() {
        return getResults("/v2/backgrounds/");
    }

    private List<Map<String, Object>> getResults(String path) {
        try {
            Object response = RestClient.builder().baseUrl(baseUrl).build()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(path).queryParam("limit", 1000).build())
                    .retrieve()
                    .body(Object.class);
            if (!(response instanceof Map<?, ?> map) || !(map.get("results") instanceof List<?> results)) {
                throw new IllegalStateException("Open5e returned an invalid response for " + path);
            }
            return objectMapper.convertValue(results, new TypeReference<>() {});
        } catch (RestClientException exception) {
            throw new IllegalStateException("Unable to load character data from Open5e: " + path, exception);
        }
    }
}
