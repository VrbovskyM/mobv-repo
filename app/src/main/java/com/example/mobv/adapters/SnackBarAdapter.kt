package com.example.mobv.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.mobv.data.services.StatusAndMessageResponse
import com.example.mobv.utils.Evento
import com.google.android.material.snackbar.Snackbar

@BindingAdapter("snackbarMessage")
fun SnackBarAdapter(view: View, message: Any?) {
    when (message) {
        is Evento<*> -> {
            // Handle the Event case
            message.getContentIfNotHandled()?.let { content ->
                if (content is StatusAndMessageResponse) {
                    Snackbar.make(view, content.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        is String -> {
            // Handle the plain String case
            if (message.isNotEmpty()) {
                Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            }
        }
        is StatusAndMessageResponse -> {
            // Handle the StatusAndMessageResponse case
            Snackbar.make(view, message.message, Snackbar.LENGTH_SHORT).show()
        }
    }
}