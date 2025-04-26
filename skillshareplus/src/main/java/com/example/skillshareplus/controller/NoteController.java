package com.example.skillshareplus.controller;

import com.example.skillshareplus.dto.request.NoteRequest;
import com.example.skillshareplus.dto.response.MessageResponse;
import com.example.skillshareplus.dto.response.NoteResponse;
import com.example.skillshareplus.model.Note;
import com.example.skillshareplus.model.Role;
import com.example.skillshareplus.model.User;
import com.example.skillshareplus.repository.NoteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteRepository noteRepository;

    @GetMapping
    public ResponseEntity<?> getAllNotes(@AuthenticationPrincipal User currentUser) {
        List<NoteResponse> notes = noteRepository.findByUserId(currentUser.getId()).stream()
                .map(note -> NoteResponse.builder()
                        .id(note.getId())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .createdAt(note.getCreatedAt())
                        .updatedAt(note.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<?> searchNoteById(@PathVariable String id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        
        NoteResponse noteResponse = NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
                
        return ResponseEntity.ok(noteResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        
        NoteResponse noteResponse = NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
                
        return ResponseEntity.ok(noteResponse);
    }

    @PostMapping
    public ResponseEntity<?> createNote(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal User currentUser) {
        // Check if user has admin role
        if (!currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            return ResponseEntity.status(403).body(new MessageResponse("Only admins can create notes"));
        }

        Note note = Note.builder()
                .title(noteRequest.getTitle())
                .content(noteRequest.getContent())
                .userId(currentUser.getId())
                .build();
        
        Note savedNote = noteRepository.save(note);
        
        NoteResponse noteResponse = NoteResponse.builder()
                .id(savedNote.getId())
                .title(savedNote.getTitle())
                .content(savedNote.getContent())
                .createdAt(savedNote.getCreatedAt())
                .updatedAt(savedNote.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(noteResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable String id, 
            @Valid @RequestBody NoteRequest noteRequest, 
            @AuthenticationPrincipal User currentUser) {
        
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        
        // Check if the note belongs to the current user
        if (!note.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(new MessageResponse("You don't have permission to update this note"));
        }
        
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        
        Note updatedNote = noteRepository.save(note);
        
        NoteResponse noteResponse = NoteResponse.builder()
                .id(updatedNote.getId())
                .title(updatedNote.getTitle())
                .content(updatedNote.getContent())
                .createdAt(updatedNote.getCreatedAt())
                .updatedAt(updatedNote.getUpdatedAt())
                .build();
        
        return ResponseEntity.ok(noteResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        
        // Check if the note belongs to the current user
        if (!note.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(new MessageResponse("You don't have permission to delete this note"));
        }
        
        noteRepository.delete(note);
        
        return ResponseEntity.ok(new MessageResponse("Note deleted successfully"));
    }
}