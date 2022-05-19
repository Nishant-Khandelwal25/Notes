package com.example.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Insert
    suspend fun insertNote(notes: Notes)

    @Update
    suspend fun updateNote(notes: Notes)

    @Delete
    suspend fun deleteNote(notes: Notes)

    @Query("Select * from notes ORDER BY date DESC")
    fun getAllNotes(): LiveData<List<Notes>>

    @Query("Select * from notes where id like :id")
    fun getNote(id: Int): LiveData<Notes>
}
