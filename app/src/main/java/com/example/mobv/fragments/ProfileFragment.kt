package com.example.mobv.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mobv.R
import com.example.mobv.data.DataRepository
import com.example.mobv.viewModels.AuthViewModel
import com.example.mobv.viewModels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    // Initialize ViewModel
    private lateinit var viewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel

    // Declare UI components
    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView
    private lateinit var logoutButton: ImageView
    private lateinit var changePasswordButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        // Initialize UI components
        profileImage = view.findViewById(R.id.profile_image)
        usernameText = view.findViewById(R.id.username_text)
        logoutButton = view.findViewById(R.id.logout_button)
        changePasswordButton = view.findViewById(R.id.change_password_button)


        authViewModel.loginResult.observe(viewLifecycleOwner){ userPair ->
            usernameText.text = userPair.second?.username ?: "Unknown"
            Glide.with(this)
                .load(userPair.second?.photo)
                .circleCrop()
                .placeholder(R.drawable.default_profile_photo)
                .into(profileImage)
        }
        authViewModel.changePasswordResult.observe(viewLifecycleOwner) { result ->
            if (result.status == "success") {
                Snackbar.make(requireView(), "Password changed successfully", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(requireView(), "Failed to change password", Snackbar.LENGTH_SHORT).show()
            }
        }
        // Set up button listeners
        logoutButton.setOnClickListener {
            authViewModel.logout()
            requireView().findNavController().navigate(R.id.action_logout)
        }

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        // Create a new Dialog instance
        val dialog = Dialog(requireContext())

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.dialog_change_password)

        // Find views in the dialog layout
        val oldPasswordEditText = dialog.findViewById<EditText>(R.id.old_password)
        val newPasswordEditText = dialog.findViewById<EditText>(R.id.new_password)
        val confirmPasswordEditText = dialog.findViewById<EditText>(R.id.confirm_password)
        val confirmButton = dialog.findViewById<Button>(R.id.confirm_button)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)

        // Set up button listeners
        confirmButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            handleChangePassword(oldPassword, newPassword, confirmPassword)
            dialog.dismiss() // Close the dialog after handling the change password
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog without any action
        }

        // Show the dialog
        dialog.show()

        // Optional: Adjust the layout parameters (like width, height, and dim effect)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun handleChangePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        if (newPassword == confirmPassword) {
            authViewModel.changePassword(oldPassword, newPassword)
            Snackbar.make(requireView(), "Password changed successfully", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(requireView(), "Passwords do not match", Snackbar.LENGTH_SHORT).show()
        }
    }
}