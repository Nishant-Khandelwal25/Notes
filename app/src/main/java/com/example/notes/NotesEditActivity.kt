package com.example.notes

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProvider
import com.example.notes.database.Notes
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.ActivityNotesEditBinding
import com.example.notes.repository.MainRepository
import com.example.notes.viewmodel.MainViewModelFactory
import com.example.notes.viewmodel.MainViewModel
import java.util.*

private const val TAG: String = "NotesEditActivity"

class NotesEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesEditBinding
    private lateinit var mainViewModel: MainViewModel
    private var noteText: String = ""
    private var noteTitle: String = ""
    private var tempNoteText = ""
    private var tempNoteTitle = ""
    private var idIntent: Int? = null
    private var dateIntent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_notes_edit)

        val dao = NotesDatabase.getDBInstance(applicationContext).notesDao()
        val repository = MainRepository(dao)

        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        idIntent = intent.getIntExtra("id", -1)

        dateIntent = intent.getStringExtra("date")

        binding.noteDateTxtView.text = dateIntent

        Log.d(TAG, "ID: $dateIntent")

    }


    override fun onResume() {
        super.onResume()

        binding.noteText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.getCount(binding.noteText.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        if (idIntent!! > 0) {
            mainViewModel.getNote(idIntent!!).observe(this, {
                updateNoteLayout(it)
            })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_note_menu_btn -> {
                noteTitle = binding.noteTitle.text.toString()
                noteText = binding.noteText.text.toString()

                titleTextEmpty()
                when {
                    noteTextEmpty() -> {
                        Toast.makeText(this, "Please add something to the note", Toast.LENGTH_LONG)
                            .show()
                    }
                    isExistingNote() -> {
                        if (isNoteUpdated()) {
                            updateExistingNote()
                        } else {
                            startMainActivity()
                        }
                    }
                    else -> {
                        createNewNote()
                    }
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        noteText = binding.noteText.text.toString()
        noteTitle = binding.noteTitle.text.toString()
        if (isExistingNote()) {
            if (isNoteUpdated()) {
                updateExistingNote()
            } else {
                startMainActivity()
            }
        } else if (noteTextEmpty()) {
            startMainActivity()
        } else {
            createNewNote()
        }
    }

    private fun titleTextEmpty() {
        if (noteTitle.isEmpty())
            noteTitle = "Title"
    }

    private fun noteTextEmpty(): Boolean {
        if (noteText.isEmpty()) {
            return true
        }
        return false
    }


    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateNoteLayout(notes: Notes) {
        tempNoteText = notes.notesText
        tempNoteTitle = notes.notesTitle
        binding.noteText.setText(notes.notesText)
        binding.noteTitle.setText(notes.notesTitle)
    }

    private fun isExistingNote(): Boolean {
        if (idIntent!! > 0) {
            return true
        }
        return false
    }

    private fun updateExistingNote() {
        titleTextEmpty()
        val note = Notes(idIntent!!, noteTitle, noteText, Date())
        mainViewModel.updateNote(note)
        startMainActivity()
    }

    private fun createNewNote() {
        titleTextEmpty()
        val note = Notes(0, noteTitle, noteText, Date())
        mainViewModel.insertNote(note)
        startMainActivity()
    }

    private fun isNoteUpdated(): Boolean {
        noteText = binding.noteText.text.toString()
        noteTitle = binding.noteTitle.text.toString()
        if ((tempNoteText.equals(noteText, true))
            && (tempNoteTitle.equals(noteTitle, true))
        ) {
            return false
        }
        return true
    }
}
