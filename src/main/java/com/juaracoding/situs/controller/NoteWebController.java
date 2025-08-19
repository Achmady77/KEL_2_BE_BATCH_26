package com.juaracoding.situs.controller;

import com.juaracoding.situs.model.Note;
import com.juaracoding.situs.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import java.io.IOException;

@Controller
@RequestMapping("/notes")
public class NoteWebController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public String listNotes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());
        return "notes/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        return "notes/form";
    }

    @PostMapping
    public String saveNote(@ModelAttribute("note") Note note,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Validate file type (JPG)
                if (!"image/jpeg".equals(imageFile.getContentType())) {
                    redirectAttributes.addFlashAttribute("message", "Error saving note: Only JPG image files are allowed.");
                    redirectAttributes.addFlashAttribute("messageType", "danger");
                    return "redirect:/notes";
                }
                // Validate file size (max 5MB)
                if (imageFile.getSize() > 5 * 1024 * 1024) { // 5MB in bytes
                    redirectAttributes.addFlashAttribute("message", "Error saving note: Image file size cannot exceed 5MB.");
                    redirectAttributes.addFlashAttribute("messageType", "danger");
                    return "redirect:/notes";
                }
            }
            noteService.saveNote(note, imageFile);
            redirectAttributes.addFlashAttribute("message", "Note saved successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Error uploading image: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error saving note: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/notes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Note> note = noteService.getNoteById(id);
        if (note.isPresent()) {
            model.addAttribute("note", note.get());
            return "notes/form";
        } else {
            redirectAttributes.addFlashAttribute("message", "Note not found!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/notes";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("message", "Note deleted successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error deleting note: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/notes";
    }
}
