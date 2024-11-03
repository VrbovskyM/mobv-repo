package com.example.mobv.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.mobv.MyApplication

class Utils {
    companion object {
        private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        // Static method to check permissions
        fun hasPermissions(): Boolean {
            return PERMISSIONS_REQUIRED.all {
                ContextCompat.checkSelfPermission(MyApplication.getContext(), it) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}
