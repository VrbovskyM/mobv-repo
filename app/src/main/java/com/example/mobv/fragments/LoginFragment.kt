package com.example.mobv.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.mobv.R
import com.example.mobv.data.DataRepository
import com.example.mobv.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var forgotPasswordBtn = view.findViewById<Button>(R.id.forgot_password_btn)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]

        viewModel.loginResult.observe(viewLifecycleOwner){ result ->
            if (result.second != null && result.second?.id != "-1"){
                requireView().findNavController().navigate(R.id.action_LoginFragment_to_ProfileFragment)
            }else{
                Snackbar.make(
                    view.findViewById(R.id.login_btn),
                    result.first,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.resetPasswordResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                Snackbar.make(
                    view.findViewById(R.id.login_btn),
                    result.message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        view.findViewById<TextView>(R.id.login_btn).apply {
            setOnClickListener {
                viewModel.loginUser(
                    view.findViewById<EditText>(R.id.editText1).text.toString(),
                    view.findViewById<EditText>(R.id.editText2).text.toString()
                )
            }
        }
        forgotPasswordBtn.setOnClickListener {
            showResetPasswordDialog()
        }
    }
    private fun showResetPasswordDialog() {
        // Create a new Dialog instance
        val dialog = Dialog(requireContext())

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.forgot_password_dialog)

        // Find views in the dialog layout
        val emailEditText = dialog.findViewById<EditText>(R.id.email_edittext)
        val confirmButton = dialog.findViewById<Button>(R.id.confirm_button)
        val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)

        // Set up button listeners
        confirmButton.setOnClickListener {
            val email = emailEditText.text.toString()
            handleChangePassword(email)
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

    private fun handleChangePassword(email: String) {
        if (email.isNotEmpty()) {
            viewModel.resetPassword(email)
        } else {
            Snackbar.make(requireView(), "Email can not be empty", Snackbar.LENGTH_SHORT).show()
        }
    }
}