package com.juaracoding.situs.controller;

import com.juaracoding.situs.model.Note;
import com.juaracoding.situs.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteApiController {

    @Autowired
    private NoteService noteService;

    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createNote(@RequestPart("note") Note note,
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
            Note savedNote = noteService.saveNote(note, imageFile);
            return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error uploading image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating note: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable Long id,
            @RequestPart("note") Note noteDetails,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        Optional<Note> existingNoteOpt = noteService.getNoteById(id);
        if (existingNoteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found.");
        }

        Note existingNote = existingNoteOpt.get();
        existingNote.setTitle(noteDetails.getTitle());
        existingNote.setContent(noteDetails.getContent());

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

            Note updatedNote = noteService.saveNote(existingNote, imageFile);
            return ResponseEntity.ok(updatedNote);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating note: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        return noteService.getNoteById(id)
                .map(note -> {
                    noteService.deleteNote(note.getId());
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
