package com.example.mobv.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mobv.MyApplication
import com.example.mobv.R
import com.example.mobv.data.DataRepository
import com.example.mobv.data.PreferenceData
import com.example.mobv.data.models.ScheduledTime
import com.example.mobv.data.models.SharingMode
import com.example.mobv.databinding.DialogChangePasswordBinding
import com.example.mobv.databinding.ForgotPasswordDialogBinding
import com.example.mobv.databinding.FragmentLoginBinding
import com.example.mobv.databinding.FragmentProfileBinding
import com.example.mobv.viewModels.AuthViewModel
import com.example.mobv.viewModels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    // ViewModels
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel

    // UI components
    private lateinit var profileImage: ImageView
    private lateinit var usernameText: TextView

    // Bindings
    private var profileBinding: FragmentProfileBinding? = null
    private var authBinding: DialogChangePasswordBinding? = null

    private var changePasswordDialog: Dialog? = null
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted){
            startLocationTracking()
        }else{
            // run logic if user not accepted usage of gps location
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBinding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            profileModel = profileViewModel
            profileFragment  = this@ProfileFragment
        }
        // Initialize UI components
        profileImage = view.findViewById(R.id.profile_image)
        usernameText = view.findViewById(R.id.username_text)


        authViewModel.authResult.observe(viewLifecycleOwner){ userPair ->
            usernameText.text = userPair.second?.username ?: "Unknown"
            val photoUrl = userPair.second?.photo
            if (!photoUrl.isNullOrBlank()) {
                // Only load with Glide if the photo URL is not empty or null
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(profileImage)
            }
        }
        if (!hasPermissions()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        profileViewModel.sharingMode.postValue(PreferenceData.getInstance().getSharingMode())
        profileViewModel.manualSharingEnabled.postValue(PreferenceData.getInstance().isManualSharing())
        profileViewModel.scheduledSharing.postValue(PreferenceData.getInstance().getScheduledTime())

        profileBinding!!.sharingModeToggle.check(
            when (PreferenceData.getInstance().getSharingMode()) {
                SharingMode.SCHEDULED -> R.id.mode_scheduled
                SharingMode.MANUAL -> R.id.mode_manual
            }
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        profileBinding = null
        authBinding = null
    }

    fun logout() {
        authViewModel.logout()
        requireView().findNavController().navigate(R.id.action_logout)
    }

    fun showChangePasswordDialog() {
        // Create a new Dialog instance
        changePasswordDialog = Dialog(requireContext())

        authBinding = DialogChangePasswordBinding.inflate(LayoutInflater.from(context)).apply {
            lifecycleOwner = viewLifecycleOwner
            profileModel = profileViewModel
            profileFragment  = this@ProfileFragment
        }
        changePasswordDialog?.setContentView(authBinding?.root!!)

        // Show the dialog
        changePasswordDialog?.show()

        // Optional: Adjust the layout parameters (like width, height, and dim effect)
        changePasswordDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun onChangeClickConfirmed() {
        profileViewModel.changePassword()
        closeChangePasswordDialog()
    }

    fun closeChangePasswordDialog() {
        changePasswordDialog?.dismiss()
        changePasswordDialog = null
    }

    // returns if Permissions are accepted
    fun hasPermissions() = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(MyApplication.getContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationTracking() {
        Snackbar.make(requireView(), "Location tracking started", Snackbar.LENGTH_SHORT).show()
    }

    fun handleManualSharing() {
        val isManualSharingEnabled = profileViewModel.manualSharingEnabled.value ?: false
        if (isManualSharingEnabled && !hasPermissions()){
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            profileViewModel.manualSharingEnabled.postValue(false)
        }
        else if (isManualSharingEnabled){
            PreferenceData.getInstance().putManualSharing(true)
        }
        else{
            PreferenceData.getInstance().putManualSharing(false)
        }
    }

    fun handleScheduledSharing() {
        val scheduledTime = profileViewModel.scheduledSharing.value ?: ScheduledTime(0,0,0,0)
        if (scheduledTime.isSameTime()){
            PreferenceData.getInstance().putScheduledTime(ScheduledTime(0,0,0,0))
        }
        else if (!scheduledTime.isSameTime() && !hasPermissions()){
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            profileViewModel.scheduledSharing.postValue(ScheduledTime(0,0,0,0))
        }
        else if (!scheduledTime.isSameTime()){
            PreferenceData.getInstance().putScheduledTime(scheduledTime)
        }
        else{
            profileViewModel.scheduledSharing.postValue(ScheduledTime(0,0,0,0))
        }
    }

    fun locationSharingMode(mode: SharingMode){
        PreferenceData.getInstance().putSharingMode(mode)
        profileViewModel.sharingMode.postValue(mode)
    }
}
