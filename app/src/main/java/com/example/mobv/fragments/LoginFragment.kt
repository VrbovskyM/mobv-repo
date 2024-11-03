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
import com.example.mobv.databinding.ForgotPasswordDialogBinding
import com.example.mobv.databinding.FragmentLoginBinding
import com.example.mobv.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private var binding: FragmentLoginBinding? = null
    private var dialogBinding: ForgotPasswordDialogBinding? = null  // Add this line
    private var resetPasswordDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            loginFragment  = this@LoginFragment
        }

        viewModel.authResult.observe(viewLifecycleOwner){ result ->
            if (result.second != null && result.second?.id != "-1"){
                requireView().findNavController().navigate(R.id.action_LoginFragment_to_ProfileFragment)
            }
        }
    }

    fun showResetPasswordDialog() {
        // Create a new Dialog instance
        resetPasswordDialog = Dialog(requireContext())

        // Initialize dialog binding
        dialogBinding = ForgotPasswordDialogBinding.inflate(LayoutInflater.from(context)).apply {
            model = viewModel
            loginFragment = this@LoginFragment
            lifecycleOwner = viewLifecycleOwner
        }

        // Set the custom layout for the dialog using the binding's root
        resetPasswordDialog?.setContentView(dialogBinding?.root!!)

        // Show the dialog
        resetPasswordDialog?.show()

        // Optional: Adjust the layout parameters (like width, height, and dim effect)
        resetPasswordDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun onResetClickConfirmed() {
        viewModel.resetPassword()
        closeResetPasswordDialog()
    }

    fun closeResetPasswordDialog() {
        resetPasswordDialog?.dismiss()
        resetPasswordDialog = null
        dialogBinding = null  // Clean up the binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        dialogBinding = null  // Clean up the binding
    }
}