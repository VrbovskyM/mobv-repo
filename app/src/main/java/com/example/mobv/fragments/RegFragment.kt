package com.example.mobv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobv.R

/**
 * A simple [Fragment] subclass.
 * Use the [RegFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg, container, false)

        view.findViewById<Button>(R.id.reg_btn).setOnClickListener {
            findNavController().navigate(R.id.action_RegFragment_to_ProfileFragment)
        }
        return view
    }

}