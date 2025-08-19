package com.juaracoding.situs.service;


/*
IntelliJ IDEA 2025.1.2 (Ultimate Edition)
Build #IU-251.26094.121, built on June 3, 2025
@Author lenovo Achmadi Suryo Utomo
Java Developer
Created on 14/08/2025 16:06
@Last Modified 14/08/2025 16:06
Version 1.0
*/

import com.juaracoding.situs.model.Situs;
import com.juaracoding.situs.repository.SitusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SitusService {
    @Autowired
    private SitusRepository situsRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public List<Situs> getAllSitus() {
        return situsRepository.findAll();
    }

    public Optional<Situs> getSitusById(Long id) {
        return situsRepository.findById(id);
    }

    public Situs saveSitus(Situs situs, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);
            // Perbaikan: Ganti setImageUrl dengan setImagePath
            situs.setImagePath("/uploads/images/" + fileName);
//            situs.setImageUrl("/uploads/images/" + fileName); // Store relative path with /uploads/images/
        }

        return situsRepository.save(situs);
    }



    public void deleteSitus(Long id) {
        situsRepository.deleteById(id);
    }


}
