package com.km9.notes.api.controller

import com.km9.notes.api.dto.NoteRequest
import com.km9.notes.api.dto.NoteResponse
import com.km9.notes.service.NoteService
import com.km9.notes.shared.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService
) {

    @PostMapping
    fun createNote(
        @Valid @RequestBody body: NoteRequest
    ): ResponseEntity<ApiResponse<NoteResponse>> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = noteService.createNote(body, ownerId)

        val response = ApiResponse.success(
            data = note,
            message = "Note created successfully"
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping
    fun updateNote(
        @Valid @RequestBody body: NoteRequest
    ): ResponseEntity<ApiResponse<NoteResponse>> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = noteService.updateNote(body, ownerId)

        val response = ApiResponse.success(
            data = note,
            message = "Note updated successfully"
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllNotes(): ResponseEntity<ApiResponse<List<NoteResponse>>> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val notes = noteService.findNotesByOwnerId(ownerId)

        val response = ApiResponse.success(
            data = notes,
            message = "Notes retrieved successfully"
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getNoteById(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<NoteResponse>> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = noteService.findNoteById(id, ownerId)

        val response = ApiResponse.success(
            data = note,
            message = "Note retrieved successfully"
        )

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteNote(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        noteService.deleteNote(id, ownerId)

        val response = ApiResponse.success<Nothing>(
            message = "Note deleted successfully"
        )

        return ResponseEntity.ok(response)
    }
}



