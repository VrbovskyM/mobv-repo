package com.example.mobv.fragments

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.mobv.R
import com.example.mobv.data.DataRepository
import com.example.mobv.data.PreferenceData
import com.example.mobv.data.models.ScheduledTime
import com.example.mobv.data.models.SharingMode
import com.example.mobv.databinding.DialogChangePasswordBinding
import com.example.mobv.databinding.FragmentProfileBinding
import com.example.mobv.utils.Utils
import com.example.mobv.viewModels.AuthViewModel
import com.example.mobv.viewModels.ProfileViewModel

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
    private lateinit var startHourPicker: NumberPicker
    private lateinit var endHourPicker: NumberPicker

    // Bindings
    private var profileBinding: FragmentProfileBinding? = null
    private var authBinding: DialogChangePasswordBinding? = null

    private var changePasswordDialog: Dialog? = null
    private var requestedSharingMode: SharingMode? = null
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted){
            val mode = requestedSharingMode?:PreferenceData.getInstance().getSharingMode()
            setSharingMode(mode)
        }else{
            setSharingMode(SharingMode.OFF)
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
        if (!Utils.hasPermissions(requireContext())) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        profileViewModel.sharingMode.postValue(PreferenceData.getInstance().getSharingMode())
        profileViewModel.scheduledSharing.postValue(PreferenceData.getInstance().getScheduledTime())

        profileBinding!!.sharingModeToggle.check(
            when (PreferenceData.getInstance().getSharingMode()) {
                SharingMode.SCHEDULED -> R.id.mode_scheduled
                SharingMode.ON -> R.id.mode_on
                else -> R.id.mode_off
            }
        )
        initializeTimePickers(view)
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

    fun handleSharingModeChange(mode: SharingMode){
        if (mode == profileViewModel.sharingMode.value) return
        requestedSharingMode = mode
        if (mode == SharingMode.ON || mode == SharingMode.SCHEDULED){
            if (!Utils.hasPermissions(requireContext())){
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else
                setSharingMode(mode)
        }
        else
            setSharingMode(SharingMode.OFF)
    }

    private fun setSharingMode(mode: SharingMode){
        if(mode == SharingMode.SCHEDULED) {
            val scheduledTime = PreferenceData.getInstance().getScheduledTime()?:ScheduledTime(0,0)
            PreferenceData.getInstance().updateScheduledTime(scheduledTime)
            profileViewModel.scheduledSharing.postValue(scheduledTime)
        }
        if (mode == SharingMode.OFF){
            profileViewModel.deleteUserLocation()
        }
        PreferenceData.getInstance().putSharingMode(mode)
        profileViewModel.sharingMode.postValue(mode)
    }

    private fun initializeTimePickers(view: View) {
        // Retrieve scheduled time
        val scheduledTime = PreferenceData.getInstance().getScheduledTime()

        startHourPicker = view.findViewById(R.id.start_hour_picker)
        endHourPicker = view.findViewById(R.id.end_hour_picker)

        startHourPicker.apply {
            minValue = 0
            maxValue = 23
            value = scheduledTime?.startHour ?: 9 // Set value from scheduledTime or default to 9

            // Set the formatter to show two digits
            this.setFormatter { value -> String.format("%02d", value) }
        }

        endHourPicker.apply {
            minValue = 0
            maxValue = 23
            value = scheduledTime?.endHour ?: 17 // Set value from scheduledTime or default to 17

            // Set the formatter to show two digits
            this.setFormatter { value -> String.format("%02d", value) }
        }
    }

    fun hourChange(isStartHour: Boolean, oldVal: Int, newVal: Int) {
        if (oldVal == newVal) return
        val scheduledTime = PreferenceData.getInstance().getScheduledTime() ?: ScheduledTime(0,0)

        // Update the appropriate field based on which hour is being changed
        if (isStartHour) {
            scheduledTime.startHour = newVal
            scheduledTime.endHour = scheduledTime.endHour ?: 17
        } else {
            scheduledTime.endHour = newVal
            scheduledTime.startHour = scheduledTime.startHour ?: 9
        }

        PreferenceData.getInstance().updateScheduledTime(scheduledTime)
    }
}
