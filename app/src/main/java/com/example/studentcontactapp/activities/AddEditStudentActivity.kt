package com.example.studentcontactapp.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studentcontactapp.database.AppDatabase
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.ActivityAddEditBinding
import kotlinx.coroutines.launch

class AddEditStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBinding
    private lateinit var db: AppDatabase

    private var studentId: Int = -1
    private var isEditMode = false

    companion object {
        const val EXTRA_STUDENT_ID = "extra_student_id"

        val PRODI_LIST = listOf(
            "Teknik Informatika",
            "Sistem Informasi",
            "Teknik Komputer",
            "Ilmu Komputer",
            "Manajemen Informatika"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)
        studentId = intent.getIntExtra(EXTRA_STUDENT_ID, -1)
        isEditMode = studentId != -1

        setupToolbar()
        setupProdiSpinner()

        if (isEditMode) {
            loadStudentData()
        }

        binding.btnSave.setOnClickListener {
            saveStudent()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (isEditMode) "Edit Mahasiswa" else "Tambah Mahasiswa"
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupProdiSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, PRODI_LIST)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProdi.adapter = adapter
    }

    private fun loadStudentData() {
        lifecycleScope.launch {
            val student = db.studentDao().getStudentById(studentId)
            student?.let {
                binding.etName.setText(it.name)
                binding.etNim.setText(it.nim)
                binding.etEmail.setText(it.email)
                binding.etSemester.setText(it.semester.toString())
                val prodiIndex = PRODI_LIST.indexOf(it.prodi)
                if (prodiIndex >= 0) binding.spinnerProdi.setSelection(prodiIndex)
            }
        }
    }

    private fun saveStudent() {
        val name = binding.etName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val semesterStr = binding.etSemester.text.toString().trim()
        val prodi = binding.spinnerProdi.selectedItem.toString()

        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Nama tidak boleh kosong"
            isValid = false
        } else binding.tilName.error = null

        if (nim.isEmpty()) {
            binding.tilNim.error = "NIM tidak boleh kosong"
            isValid = false
        } else binding.tilNim.error = null

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email tidak valid"
            isValid = false
        } else binding.tilEmail.error = null

        val semester = semesterStr.toIntOrNull()
        if (semester == null || semester < 1 || semester > 14) {
            binding.tilSemester.error = "Semester tidak valid (1-14)"
            isValid = false
        } else binding.tilSemester.error = null

        if (!isValid) return

        lifecycleScope.launch {
            if (isEditMode) {
                val updated = StudentEntity(
                    id = studentId,
                    name = name,
                    nim = nim,
                    prodi = prodi,
                    email = email,
                    semester = semester!!
                )
                db.studentDao().update(updated)
                Toast.makeText(this@AddEditStudentActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            } else {
                val student = StudentEntity(
                    name = name,
                    nim = nim,
                    prodi = prodi,
                    email = email,
                    semester = semester!!
                )
                db.studentDao().insert(student)
                Toast.makeText(this@AddEditStudentActivity, "Mahasiswa berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}