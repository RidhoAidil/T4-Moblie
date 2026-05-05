package com.example.studentcontactapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.studentcontactapp.R
import com.example.studentcontactapp.activities.LoginActivity
import com.example.studentcontactapp.databinding.FragmentProfileBinding
import com.example.studentcontactapp.utils.PrefManager
import com.example.studentcontactapp.utils.SettingsManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefManager: PrefManager
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())
        settingsManager = SettingsManager(requireContext())

        binding.tvUsername.text = prefManager.username
        binding.tvInitials.text = prefManager.username.take(2).uppercase()

        binding.switchDarkMode.isChecked = settingsManager.darkMode
        binding.switchNotification.isChecked = settingsManager.notificationEnabled

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.darkMode = isChecked
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.notificationEnabled = isChecked
        }

        binding.btnLogout.setOnClickListener {
            prefManager.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        binding.btnNoteList.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, NoteListFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}