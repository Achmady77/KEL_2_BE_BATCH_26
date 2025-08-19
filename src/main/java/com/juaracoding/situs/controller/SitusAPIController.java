package com.juaracoding.situs.controller;


/*
IntelliJ IDEA 2025.1.2 (Ultimate Edition)
Build #IU-251.26094.121, built on June 3, 2025
@Author lenovo Achmadi Suryo Utomo
Java Developer
Created on 14/08/2025 16:24
@Last Modified 14/08/2025 16:24
Version 1.0
*/

import com.juaracoding.situs.model.Situs;
import com.juaracoding.situs.model.Situs;
import com.juaracoding.situs.service.SitusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/situs")
public class SitusAPIController {
    @Autowired
    private SitusService situsService;

    @GetMapping
    public List<Situs> getAllSitus() {
        return situsService.getAllSitus();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Situs> getSitusById(@PathVariable Long id) {
        return situsService.getSitusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createSitus(@RequestPart("situs") Situs situs,
                                             @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                if (!"image/jpeg".equals(imageFile.getContentType())) {
                    return new ResponseEntity<>("Only JPG image files are allowed.", HttpStatus.BAD_REQUEST);
                }
                if (imageFile.getSize() > 5 * 1024 * 1024) { // 5MB in bytes
                    return new ResponseEntity<>("Image file size cannot exceed 5MB.", HttpStatus.BAD_REQUEST);
                }
            }
            Situs savedSitus = situsService.saveSitus(situs, imageFile);
            return new ResponseEntity<>(savedSitus, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error uploading image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating situs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSitus(
            @PathVariable Long id,
            @RequestPart("situs") Situs situsDetails,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        Optional<Situs> existingSitusOpt = situsService.getSitusById(id);
        if (existingSitusOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Situs not found.");
        }

        Situs existingSitus = existingSitusOpt.get();
        existingSitus.setTitle(situsDetails.getTitle());
        existingSitus.setContent(situsDetails.getContent());

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String contentType = imageFile.getContentType();
                if (!"image/jpeg".equalsIgnoreCase(contentType) && !"image/jpg".equalsIgnoreCase(contentType)) {
                    return ResponseEntity.badRequest().body("Only JPG image files are allowed.");
                }
                if (imageFile.getSize() > 5 * 1024 * 1024) { // 5MB
                    return ResponseEntity.badRequest().body("Image file size cannot exceed 5MB.");
                }
            }

            Situs updatedSitus = situsService.saveSitus(existingSitus, imageFile);
            return ResponseEntity.ok(updatedSitus);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating situs: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSitus(@PathVariable Long id) {
        return situsService.getSitusById(id)
                .map(situs -> {
                    situsService.deleteSitus(situs.getId());
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


