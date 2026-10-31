package com.ttrp.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.location:./storage}")
    private String storageLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path storageDir = Paths.get(storageLocation).toAbsolutePath().normalize();

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + storageDir.toString() + "/");
    }
}
