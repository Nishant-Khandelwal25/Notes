package com.example.notes.repository

import androidx.lifecycle.LiveData
import com.example.notes.database.Notes
import com.example.notes.database.NotesDao

class MainRepository(private val notesDao: NotesDao) {

    suspend fun insertNote(notes: Notes) {
        notesDao.insertNote(notes)
    }

    suspend fun updateNote(notes: Notes) {
        notesDao.updateNote(notes)
    }

    suspend fun deleteNote(notes: Notes) {
        notesDao.deleteNote(notes)
    }

    fun getAllNotes(): LiveData<List<Notes>> {
        return notesDao.getAllNotes()
    }

    fun getNote(id: Int): LiveData<Notes> {
        return notesDao.getNote(id)
    }
}