package com.example.mobv

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

class BottomBarLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.bottom_bar_layout, this, true)

        findViewById<ImageView>(R.id.ic_action_name).setOnClickListener {
            findNavController().navigate(R.id.action_btn_to_feed)
        }
        findViewById<ImageView>(R.id.img2).setOnClickListener {
            findNavController().navigate(R.id.action_btn_to_map)
        }
        findViewById<ImageView>(R.id.img3).setOnClickListener {
            findNavController().navigate(R.id.action_btn_to_profile)
        }
    }

}