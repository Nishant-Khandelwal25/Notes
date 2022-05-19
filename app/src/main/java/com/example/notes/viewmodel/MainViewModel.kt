package com.example.notes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.database.Notes
import com.example.notes.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var charCount = MutableLiveData<String>()

    lateinit var notesLiveDataList: MutableLiveData<List<Notes>>

    init {
        notesLiveDataList = MutableLiveData()
    }

    fun insertNote(notes: Notes) {
        viewModelScope.launch {
            mainRepository.insertNote(notes)
        }
    }

    fun updateNote(notes: Notes) {
        viewModelScope.launch {
            mainRepository.updateNote(notes)
        }
    }

    fun deleteNote(notes: Notes) {
        viewModelScope.launch {
            mainRepository.deleteNote(notes)
        }

    }

    fun getAllNotes(): LiveData<List<Notes>> {
        return mainRepository.getAllNotes()
    }

    fun getNote(id: Int): LiveData<Notes> {
        return mainRepository.getNote(id)
    }

//    fun getLiveDataObserver(): MutableLiveData<List<Notes>>{
//
//    }

    fun getCount(count: String){
        val mCount = count.length.toString()
        charCount.value = "Characters: $mCount"
    }
}