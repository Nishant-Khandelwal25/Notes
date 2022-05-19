package com.example.notes.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.database.Notes
import java.text.SimpleDateFormat
import java.util.*

class MainRecyclerViewAdapter(
    private val itemClickListener: OnItemClickListener,
    private val optionClickListener: OnOptionClickListener
) :
    ListAdapter<Notes, MainRecyclerViewAdapter.NotesViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false)
        return NotesViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, itemClickListener, optionClickListener)
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val noteTitle: TextView = view.findViewById(R.id.note_layout_title)
        private val noteText: TextView = view.findViewById(R.id.note_layout_text)
        private val notesMenu: TextView = view.findViewById(R.id.modify_note)
        private val noteDate: TextView = view.findViewById(R.id.note_date)


        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(
            note: Notes,
            clickListener: OnItemClickListener,
            optionClickListener: OnOptionClickListener
        ) {
            val date = note.date
            noteText.text = note.notesText
            noteTitle.text = note.notesTitle

            val formatter = SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.getDefault())
            val currentDateAndTime = formatter.format(date)

            Log.d("MainAdapter", currentDateAndTime.toString())
            noteDate.text = currentDateAndTime

            itemView.setOnClickListener {
                clickListener.onItemClicked(note)
            }

            notesMenu.setOnClickListener {
                optionClickListener.onOptionClick(note, notesMenu)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Notes>() {
        override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(note: Notes)
    }

    interface OnOptionClickListener {
        fun onOptionClick(note: Notes, view: View)
    }

}
