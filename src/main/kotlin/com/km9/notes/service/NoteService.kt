package com.km9.notes.service

import com.km9.notes.api.dto.NoteRequest
import com.km9.notes.api.dto.NoteResponse
import com.km9.notes.entity.Note
import com.km9.notes.repository.NoteRepository
import com.km9.notes.shared.ForbiddenException
import com.km9.notes.shared.ResourceNotFoundException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NoteService(
    private val repository: NoteRepository
) {

    fun createNote(request: NoteRequest, ownerId: String): NoteResponse {
        val note = Note(
            id = ObjectId.get(),
            title = request.title,
            description = request.description,
            createdAt = Instant.now(),
            ownerId = ObjectId(ownerId)
        )

        val savedNote = repository.save(note)

        return NoteResponse(
            id = savedNote.id.toHexString(),
            title = savedNote.title,
            description = savedNote.description,
            createdAt = savedNote.createdAt,
        )
    }

    fun updateNote(request: NoteRequest, ownerId: String): NoteResponse {
        val noteId = request.id
            ?: throw ResourceNotFoundException("Note ID is required for update")

        val existingNote = repository.findById(ObjectId(noteId)).orElseThrow {
            ResourceNotFoundException("Note not found with id: $noteId")
        }

        if (existingNote.ownerId.toHexString() != ownerId) {
            throw ForbiddenException("You don't have permission to update this note")
        }

        val updatedNote = existingNote.copy(
            title = request.title,
            description = request.description
        )

        val savedNote = repository.save(updatedNote)

        return NoteResponse(
            id = savedNote.id.toHexString(),
            title = savedNote.title,
            description = savedNote.description,
            createdAt = savedNote.createdAt,
        )
    }

    fun findNotesByOwnerId(ownerId: String): List<NoteResponse> {
        return repository.findByOwnerId(ObjectId(ownerId)).map {
            NoteResponse(
                id = it.id.toHexString(),
                title = it.title,
                description = it.description,
                createdAt = it.createdAt,
            )
        }
    }

    fun deleteNote(noteId: String, ownerId: String) {
        val note = repository.findById(ObjectId(noteId)).orElseThrow {
            ResourceNotFoundException("Note not found with id: $noteId")
        }

        if (note.ownerId.toHexString() != ownerId) {
            throw ForbiddenException("You don't have permission to delete this note")
        }

        repository.deleteById(ObjectId(noteId))
    }

    fun findNoteById(noteId: String, ownerId: String): NoteResponse {
        val note = repository.findById(ObjectId(noteId)).orElseThrow {
            ResourceNotFoundException("Note not found with id: $noteId")
        }

        if (note.ownerId.toHexString() != ownerId) {
            throw ForbiddenException("You don't have permission to view this note")
        }

        return NoteResponse(
            id = note.id.toHexString(),
            title = note.title,
            description = note.description,
            createdAt = note.createdAt,
        )
    }
}