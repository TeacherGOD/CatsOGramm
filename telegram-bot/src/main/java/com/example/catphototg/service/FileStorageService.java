package com.example.catphototg.service;

import com.example.catphototg.exceptions.StorageException;
import com.example.catphototg.exceptions.StorageInitializationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
            throw new StorageException("Не удалось сохранить файл: " + e.getMessage(), e);
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
            throw new StorageInitializationException("Не удалось инициализировать папку", e);
        }
    }

    public void delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;
        try {
            Path path = rootLocation.resolve(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new StorageException("Не удалось удалить файл:" + e.getMessage(), e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Не удалось прочитать файл:" + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Ошибка в URL для файла:" + filename, e);
        }
    }
}
