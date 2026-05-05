package com.example.studentcontactapp.utils

import android.content.Context
import java.io.File
import java.io.IOException

object FileHelper {

    private fun getNoteFileName(studentNim: String) = "note_$studentNim.txt"

    fun saveNote(context: Context, studentNim: String, content: String): Boolean {
        return try {
            val fileName = getNoteFileName(studentNim)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(content.toByteArray())
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun loadNote(context: Context, studentNim: String): String? {
        return try {
            val fileName = getNoteFileName(studentNim)
            context.openFileInput(fileName).use { input ->
                input.bufferedReader().readText()
            }
        } catch (e: IOException) {
            null
        }
    }

    fun deleteNote(context: Context, studentNim: String): Boolean {
        val fileName = getNoteFileName(studentNim)
        return context.deleteFile(fileName)
    }

    fun isNoteExists(context: Context, studentNim: String): Boolean {
        val fileName = getNoteFileName(studentNim)
        return context.fileList().contains(fileName)
    }

    fun getNoteSize(context: Context, studentNim: String): Long {
        val fileName = getNoteFileName(studentNim)
        val file = File(context.filesDir, fileName)
        return if (file.exists()) file.length() else 0L
    }

    fun getAllNotes(context: Context): List<Pair<String, Long>> {
        return context.fileList()
            .filter { it.startsWith("note_") && it.endsWith(".txt") }
            .map { fileName ->
                val file = File(context.filesDir, fileName)
                Pair(fileName, file.length())
            }
    }
}