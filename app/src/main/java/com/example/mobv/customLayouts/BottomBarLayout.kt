package com.example.mobv.customLayouts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.mobv.R

class BottomBarLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.bottom_bar_layout, this, true)

        findViewById<ImageView>(R.id.map).setOnClickListener {
            findNavController().navigate(R.id.action_btn_to_map)
        }
        findViewById<ImageView>(R.id.feed).setOnClickListener {
            findNavController().navigate(R.id.action_btn_to_feed)
        }
        findViewById<ImageView>(R.id.profile).setOnClickListener {
            findNavController().navigate(R.id.action_btn_to_profile)
        }
    }

}