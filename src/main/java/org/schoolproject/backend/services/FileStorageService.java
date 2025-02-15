package org.schoolproject.backend.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    // Définition du chemin où les fichiers seront stockés
    private final Path storagePath = Paths.get("resources/images");

    public FileStorageService() {
        try {
            // Création du dossier de stockage s'il n'existe pas
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage folder", e);
        }
    }

    public String storeFile(MultipartFile file, String prefix) {
        try {
            // Vérification si le fichier est vide
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Cannot store empty file");
            }

            // Vérification de la taille du fichier (limité à 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds limit of 5MB");
            }

            // Récupération et validation de l'extension du fichier
            String fileExtension = getFileExtension(file.getOriginalFilename());
            if (!isAllowedExtension(fileExtension)) {
                throw new IllegalArgumentException("Invalid file type. Allowed: jpg, png, jpeg");
            }

            // Génération d'un nom unique avec préfixe
            String fileName = prefix + "_" + UUID.randomUUID() + fileExtension;
            Path targetLocation = storagePath.resolve(fileName);

            // Copie du fichier dans le dossier de stockage
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retourne l'URL relative pour accès à l'image (c'est ce qui sera stocké en base de données)
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Resource loadFile(String fileName) {
        try {
            // Chargement du fichier depuis le stockage
            Path filePath = storagePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading file", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            // Suppression du fichier s'il existe
            Path filePath = storagePath.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }

    // Vérifie si l'extension est valide
    private boolean isAllowedExtension(String extension) {
        return extension.matches("\\.(jpg|png|jpeg)");
    }

    // Extrait l'extension du fichier
    private String getFileExtension(String filename) {
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : "";
    }
}

