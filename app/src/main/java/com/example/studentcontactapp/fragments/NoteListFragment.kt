package com.example.studentcontactapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentcontactapp.adapters.NoteListAdapter
import com.example.studentcontactapp.databinding.FragmentNoteListBinding
import com.example.studentcontactapp.utils.FileHelper

class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val notes = FileHelper.getAllNotes(requireContext())
        binding.tvEmpty.isVisible = notes.isEmpty()
        binding.rvNotes.isVisible = notes.isNotEmpty()

        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotes.adapter = NoteListAdapter(notes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}