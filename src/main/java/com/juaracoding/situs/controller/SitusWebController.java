package com.juaracoding.situs.controller;


/*
IntelliJ IDEA 2025.1.2 (Ultimate Edition)
Build #IU-251.26094.121, built on June 3, 2025
@Author lenovo Achmadi Suryo Utomo
Java Developer
Created on 14/08/2025 16:28
@Last Modified 14/08/2025 16:28
Version 1.0
*/

import com.juaracoding.situs.model.Situs;
import com.juaracoding.situs.service.SitusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import java.io.IOException;

@Controller
@RequestMapping("/situs")
public class SitusWebController {
    @Autowired
    private SitusService situsService;

    @GetMapping
    public String listSitus(Model model) {
        model.addAttribute("situs", situsService.getAllSitus());
        return "situs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("situs", new Situs());
        return "situs/form";
    }

    @PostMapping
    public String saveSitus(@ModelAttribute("situs") Situs situs,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Validate file type (JPG)
                if (!"image/jpeg".equals(imageFile.getContentType())) {
                    redirectAttributes.addFlashAttribute("message", "Error saving situs: Only JPG image files are allowed.");
                    redirectAttributes.addFlashAttribute("messageType", "danger");
                    return "redirect:/situs";
                }
                // Validate file size (max 5MB)
                if (imageFile.getSize() > 5 * 1024 * 1024) { // 5MB in bytes
                    redirectAttributes.addFlashAttribute("message", "Error saving situs: Image file size cannot exceed 5MB.");
                    redirectAttributes.addFlashAttribute("messageType", "danger");
                    return "redirect:/situs";
                }
            }
            situsService.saveSitus(situs, imageFile);
            redirectAttributes.addFlashAttribute("message", "Situs saved successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error uploading image: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error saving situs: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/situs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Situs> situs = situsService.getSitusById(id);
        if (situs.isPresent()) {
            model.addAttribute("situs", situs.get());
            return "situs/form";
        } else {
            redirectAttributes.addFlashAttribute("message", "Situs not found!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/situs";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSitus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            situsService.deleteSitus(id);
            redirectAttributes.addFlashAttribute("message", "Situs deleted successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error deleting situs: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/situs";
    }
}
