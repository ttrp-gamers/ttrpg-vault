package com.ttrp.manager.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/open5e")
@RequiredArgsConstructor
public class Open5eController {
    private static final int MAX_UPSTREAM_PAGES = 10;

    private final RestClient open5eClient = RestClient.create();
    private final ObjectMapper objectMapper;

    @GetMapping("/creatures")
    public ResponseEntity<String> searchCreatures(
            @RequestParam(defaultValue = "2014") String version,
            @RequestParam(required = false) String query
    ) throws Exception {
        String document = "2024".equals(version) ? "srd-2024" : "srd-2014";
        String uri = UriComponentsBuilder
                .fromUriString("https://api.open5e.com/v2/creatures/")
                .queryParam("document__key__in", document)
                .queryParam("page_size", 100)
                .queryParamIfPresent("name__icontains", java.util.Optional.ofNullable(query).filter(value -> !value.isBlank()))
                .toUriString();

        ObjectNode combinedResponse = objectMapper.createObjectNode();
        ArrayNode combinedResults = objectMapper.createArrayNode();
        java.util.Set<String> visitedPages = new java.util.HashSet<>();
        String nextUri = uri;

        while (nextUri != null && visitedPages.size() < MAX_UPSTREAM_PAGES && visitedPages.add(nextUri)) {
            String response = open5eClient.get().uri(nextUri).retrieve().body(String.class);
            JsonNode page = objectMapper.readTree(response);

            JsonNode results = page.get("results");
            if (results != null && results.isArray()) {
                results.forEach(combinedResults::add);
            }

            JsonNode next = page.get("next");
            nextUri = next != null && !next.isNull() && !next.asText().isBlank()
                    ? next.asText()
                    : null;
        }

        combinedResponse.set("results", combinedResults);
        combinedResponse.put("count", combinedResults.size());
        combinedResponse.putNull("next");
        combinedResponse.putNull("previous");

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(combinedResponse));
    }
}
