package com.example.studentcontactapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.ItemStudentBinding

class StudentAdapter(
    private val onItemClick: (StudentEntity) -> Unit,
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit
) : ListAdapter<StudentEntity, StudentAdapter.StudentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentEntity) {
            binding.tvInitials.text = student.initials
            binding.tvName.text = student.name
            binding.tvNim.text = student.nim
            binding.tvProdi.text = student.prodi

            binding.root.setOnClickListener { onItemClick(student) }
            binding.btnEdit.setOnClickListener { onEditClick(student) }
            binding.btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StudentEntity>() {
        override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity) =
            oldItem == newItem
    }
}