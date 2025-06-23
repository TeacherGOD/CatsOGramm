package com.example.catphototg.service;

import com.example.catphototg.exceptions.StorageException;
import com.example.catphototg.exceptions.StorageInitializationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir:uploads}")
    private Path rootLocation;

    public String store(File file) {
        try {
            String filename = UUID.randomUUID() + ".jpg";
            Files.copy(file.toPath(), rootLocation.resolve(filename));
            return filename;
        } catch (Exception e) {
            throw new StorageException("Failed to store file: " + e.getMessage(), e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageInitializationException("Could not initialize storage", e);
        }
    }
}
