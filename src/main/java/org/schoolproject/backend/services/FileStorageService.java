package org.schoolproject.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    // Définition du chemin où les fichiers seront stockés (dans Apache/XAMPP)
    private final Path storagePath = Paths.get("C:/xampp/htdocs/uploads");

    public FileStorageService() {
        try {
            // Création du dossier de stockage s'il n'existe pas
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de stockage", e);
        }
    }

    /**
     *  Stocke un fichier et retourne l'URL accessible pour l’enregistrer en base de données.
     * Si une ancienne image existe, elle est supprimée avant d’enregistrer la nouvelle.
     *
     * @param file        Le fichier à stocker
     * @param prefix      Le préfixe pour éviter les conflits (ex: user_ ou recipe_)
     * @param oldFileName L’ancien fichier à remplacer (peut être null si c’est un nouvel upload)
     * @return L’URL publique du fichier stocké
     */
    public String storeFile(MultipartFile file, String prefix, String oldFileName) {
        try {
            //  Vérifie si le fichier est vide
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Le fichier est vide.");
            }

            //  Vérifie la taille du fichier (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("La taille du fichier dépasse la limite de 5MB.");
            }

            //  Vérifie l'extension du fichier
            String fileExtension = getFileExtension(file.getOriginalFilename());
            if (!isAllowedExtension(fileExtension)) {
                throw new IllegalArgumentException("Type de fichier non autorisé. Extensions autorisées : jpg, png, jpeg.");
            }

            // Supprime l’ancien fichier s'il existe
            if (oldFileName != null) {
                deleteFile(oldFileName);
            }

            //  Génération d'un nom unique avec préfixe
            String fileName = prefix + "_" + UUID.randomUUID() + fileExtension;
            Path targetLocation = storagePath.resolve(fileName);

            // Copie du fichier dans le dossier de stockage
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            //  Retourne l’URL relative pour stockage en base de données
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Échec du stockage du fichier.", e);
        }
    }

    /**
     *  Supprime un fichier du stockage.
     * @param fileName Le nom du fichier à supprimer.
     */
    public void deleteFile(String fileName) {
        try {
            if (fileName != null && !fileName.isEmpty()) {
                Path filePath = storagePath.resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier.", e);
        }
    }

    /**
     *  Vérifie si l'extension du fichier est autorisée.
     */
    private boolean isAllowedExtension(String extension) {
        return extension.matches("\\.(jpg|png|jpeg)");
    }

    /**
     *  Extrait l'extension du fichier.
     */
    private String getFileExtension(String filename) {
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : "";
    }
}
