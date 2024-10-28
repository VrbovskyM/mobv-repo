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
import com.example.mobv.viewModels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class RegistrationFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        view.findViewById<Button>(R.id.reg_btn).setOnClickListener {
            findNavController().navigate(R.id.action_RegFragment_to_ProfileFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        viewModel.registrationResult.observe(viewLifecycleOwner){
            if (it.second != null){
                requireView().findNavController().navigate(R.id.action_RegFragment_to_ProfileFragment)
            }else{
                Snackbar.make(
                    view.findViewById(R.id.reg_btn),
                    it.first,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        view.findViewById<TextView>(R.id.reg_btn).apply {
            setOnClickListener {
                viewModel.registerUser(
                    view.findViewById<EditText>(R.id.editText1).text.toString(),
                    view.findViewById<EditText>(R.id.editText2).text.toString(),
                    view.findViewById<EditText>(R.id.editText3).text.toString()
                )
            }
        }
    }

}