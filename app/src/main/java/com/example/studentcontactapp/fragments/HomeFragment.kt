package com.example.studentcontactapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcontactapp.R
import com.example.studentcontactapp.activities.AddEditStudentActivity
import com.example.studentcontactapp.adapters.StudentAdapter
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.FragmentHomeBinding
import com.example.studentcontactapp.utils.PrefManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: StudentAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        prefManager = PrefManager(requireContext())

        binding.tvWelcome.text = "Selamat datang, ${prefManager.username}!"

        setupAdapter()
        setupRecyclerView()
        setupSwipeToDelete()
        setupFab()
        loadStudents()
    }

    private fun setupAdapter() {
        adapter = StudentAdapter(
            onItemClick = { student -> openDetail(student) },
            onEditClick = { student -> openEdit(student) },
            onDeleteClick = { student -> showDeleteDialog(student) }
        )
    }

    private fun setupRecyclerView() {
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val student = adapter.currentList[viewHolder.adapterPosition]
                showDeleteDialog(student)
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvStudents)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditStudentActivity::class.java))
        }
    }

    private fun loadStudents() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (db.studentDao().getStudentCount() == 0) {
                insertSampleData()
            }
            db.studentDao().getAllStudents().collectLatest { students ->
                adapter.submitList(students)
                binding.tvEmpty.isVisible = students.isEmpty()
            }
        }
    }

    private suspend fun insertSampleData() {
        val samples = listOf(
            StudentEntity(name = "Ahmad Fauzi", nim = "2024001", prodi = "Teknik Informatika", email = "ahmad@student.ac.id", semester = 4),
            StudentEntity(name = "Budi Santoso", nim = "2024002", prodi = "Sistem Informasi", email = "budi@student.ac.id", semester = 3),
            StudentEntity(name = "Clara Wijaya", nim = "2024003", prodi = "Teknik Informatika", email = "clara@student.ac.id", semester = 5)
        )
        db.studentDao().insertAll(samples)
    }

    // ✅ FIX: Gunakan NavController bukan parentFragmentManager manual
    private fun openDetail(student: StudentEntity) {
        findNavController().navigate(
            R.id.action_home_to_detail,
            bundleOf("studentId" to student.id)
        )
    }

    private fun openEdit(student: StudentEntity) {
        val intent = Intent(requireContext(), AddEditStudentActivity::class.java)
        intent.putExtra(AddEditStudentActivity.EXTRA_STUDENT_ID, student.id)
        startActivity(intent)
    }

    private fun showDeleteDialog(student: StudentEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"${student.name}\"? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    db.studentDao().deleteById(student.id)
                    Toast.makeText(requireContext(), "Data dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}