package com.example.mobv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mobv.R
import com.example.mobv.data.DataRepository
import com.example.mobv.databinding.FragmentLoginBinding
import com.example.mobv.databinding.FragmentRegistrationBinding
import com.example.mobv.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var viewModel: AuthViewModel
    private var regBinding: FragmentRegistrationBinding? = null

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

        regBinding = FragmentRegistrationBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            registrationFragment  = this@RegistrationFragment
        }
        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]

        viewModel.authResult.observe(viewLifecycleOwner){ result ->
            if (result.second != null){
                requireView().findNavController().navigate(R.id.action_RegFragment_to_ProfileFragment)
            }
        }
    }

}