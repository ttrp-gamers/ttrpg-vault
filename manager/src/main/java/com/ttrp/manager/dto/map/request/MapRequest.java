package com.ttrp.manager.dto.map.request;


import jakarta.validation.constraints.NotBlank;

public record MapRequest (
        @NotBlank
        String name,
        String backgroundPath
){}
