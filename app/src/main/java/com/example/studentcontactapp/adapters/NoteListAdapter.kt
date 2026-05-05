package com.example.studentcontactapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcontactapp.databinding.ItemNoteBinding

class NoteListAdapter(
    private val notes: List<Pair<String, Long>>
) : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val (fileName, size) = notes[position]
        holder.bind(fileName, size)
    }

    override fun getItemCount() = notes.size

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fileName: String, size: Long) {
            val nim = fileName.removePrefix("note_").removeSuffix(".txt")
            binding.tvNoteNim.text = "NIM: $nim"
            binding.tvNoteFile.text = fileName
            binding.tvNoteSize.text = "$size bytes"
        }
    }
}