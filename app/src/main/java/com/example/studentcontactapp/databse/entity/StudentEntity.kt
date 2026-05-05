package com.example.studentcontactapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val nim: String,
    val prodi: String,
    val email: String,
    val semester: Int,
    val createdAt: Long = System.currentTimeMillis()
) {
    val initials: String
        get() {
            val parts = name.trim().split(" ")
            return if (parts.size >= 2) {
                "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            } else {
                name.take(2).uppercase()
            }
        }
}