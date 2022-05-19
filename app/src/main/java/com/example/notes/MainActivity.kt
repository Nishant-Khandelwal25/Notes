package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.adapters.MainRecyclerViewAdapter
import com.example.notes.database.Notes
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.repository.MainRepository
import com.example.notes.viewmodel.MainViewModelFactory
import com.example.notes.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), MainRecyclerViewAdapter.OnItemClickListener,
    MainRecyclerViewAdapter.OnOptionClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: MainRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Log.d(TAG, "OnCreate()")
        binding.addNoteBtn.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.getDefault())
            val currentDateAndTime = formatter.format(Date())
            val intent = Intent(this, NotesEditActivity::class.java)
            intent.putExtra("date", currentDateAndTime)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        setAdapter()
        setViewModel()
        Log.d(TAG, "OnStart()")
    }

    private fun setAdapter() {
        adapter = MainRecyclerViewAdapter(this, this)
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = adapter
    }

    private fun setViewModel() {
        val dao = NotesDatabase.getDBInstance(applicationContext).notesDao()
        val repository = MainRepository(dao)
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
        mainViewModel.getAllNotes().observe(this, {
            Log.d(TAG, it.toString())
            adapter.submitList(it)
            registerForContextMenu(binding.notesRecyclerView)
        })
    }

    override fun onItemClicked(note: Notes) {
        val formatter = SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.getDefault())
        val currentDateAndTime = formatter.format(note.date)
        val intent = Intent(this, NotesEditActivity::class.java)
        intent.putExtra("id", note.id)
        intent.putExtra("date", currentDateAndTime)
        startActivity(intent)
        finish()
    }

    override fun onOptionClick(note: Notes, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.context_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_note -> {
                    onItemClicked(note)
                }
                R.id.delete_note -> {
                    deleteNote(note)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun deleteNote(note: Notes) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Note")
        builder.setMessage("Are you sure you want to delete note")

        builder.setPositiveButton("Yes") { _, _ ->
            mainViewModel.deleteNote(note)
        }
        builder.setNeutralButton("Cancel") { _, _ ->
        }
        builder.setNegativeButton("No") { _, _ ->
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
