package com.example.studentcontactapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.databinding.FragmentDetailBinding
import com.example.studentcontactapp.utils.FileHelper
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private var studentId: Int = -1
    private var studentNim: String = ""

    companion object {
        fun newInstance(studentId: Int): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("studentId", studentId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        // ✅ Baca studentId dari arguments (dikirim via NavController)
        studentId = arguments?.getInt("studentId", -1) ?: -1

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (studentId != -1) {
            loadStudentDetail()
        } else {
            Toast.makeText(requireContext(), "ID mahasiswa tidak valid", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveNote.setOnClickListener { saveNote() }
        binding.btnLoadNote.setOnClickListener { loadNote() }
    }

    private fun loadStudentDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            val student = db.studentDao().getStudentById(studentId)
            student?.let {
                studentNim = it.nim
                binding.tvInitials.text = it.initials
                binding.tvName.text = it.name
                binding.tvNimProdi.text = "${it.nim} · ${it.prodi}"
                binding.tvEmail.text = it.email
                binding.tvSemester.text = "Semester ${it.semester}"

                // Otomatis muat catatan jika sudah ada
                if (FileHelper.isNoteExists(requireContext(), studentNim)) {
                    val content = FileHelper.loadNote(requireContext(), studentNim)
                    binding.etNote.setText(content)
                    val size = FileHelper.getNoteSize(requireContext(), studentNim)
                    binding.tvNoteStatus.text = "✓ Catatan tersimpan ($size bytes)"
                } else {
                    binding.tvNoteStatus.text = "Belum ada catatan"
                }
            } ?: run {
                Toast.makeText(requireContext(), "Data mahasiswa tidak ditemukan", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun saveNote() {
        val content = binding.etNote.text.toString()
        if (studentNim.isEmpty()) {
            Toast.makeText(requireContext(), "Data mahasiswa belum dimuat", Toast.LENGTH_SHORT).show()
            return
        }
        val success = FileHelper.saveNote(requireContext(), studentNim, content)
        if (success) {
            val size = FileHelper.getNoteSize(requireContext(), studentNim)
            binding.tvNoteStatus.text = "✓ Tersimpan ($size bytes)"
            Toast.makeText(requireContext(), "Catatan disimpan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNote() {
        if (studentNim.isEmpty()) return
        val content = FileHelper.loadNote(requireContext(), studentNim)
        if (content != null) {
            binding.etNote.setText(content)
            val size = FileHelper.getNoteSize(requireContext(), studentNim)
            binding.tvNoteStatus.text = "✓ Catatan dimuat ($size bytes)"
        } else {
            binding.tvNoteStatus.text = "Belum ada catatan"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}