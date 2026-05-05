package com.example.studentcontactapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcontactapp.databinding.ActivityLoginBinding
import com.example.studentcontactapp.utils.PrefManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        // Jika sudah login dan remember me aktif, langsung ke MainActivity
        if (prefManager.isLoggedIn && prefManager.rememberMe) {
            goToMain()
            return
        }

        // Isi username jika remember me aktif
        if (prefManager.rememberMe) {
            binding.etUsername.setText(prefManager.username)
            binding.cbRememberMe.isChecked = true
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberMe = binding.cbRememberMe.isChecked

            when {
                username.isEmpty() -> {
                    binding.tilUsername.error = "Username tidak boleh kosong"
                }
                password.isEmpty() -> {
                    binding.tilPassword.error = "Password tidak boleh kosong"
                }
                username == "admin" && password == "123456" -> {
                    binding.tilUsername.error = null
                    binding.tilPassword.error = null
                    prefManager.saveLoginSession(username, rememberMe)
                    goToMain()
                }
                else -> {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}