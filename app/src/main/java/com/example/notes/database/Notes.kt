package com.example.notes.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val notesTitle: String,
    val notesText: String,
    val date: Date
)