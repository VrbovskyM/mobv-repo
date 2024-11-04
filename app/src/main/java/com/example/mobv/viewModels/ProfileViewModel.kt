package com.example.mobv.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobv.data.DataRepository
import com.example.mobv.data.models.User
import androidx.lifecycle.viewModelScope
import com.example.mobv.data.services.StatusAndMessageResponse
import com.example.mobv.utils.Evento
import kotlinx.coroutines.launch
import android.view.View;
import android.widget.NumberPicker
import com.example.mobv.data.models.ScheduledTime
import com.example.mobv.data.models.SharingMode

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel(){

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    val changePasswordResult = MutableLiveData<Evento<StatusAndMessageResponse>>()
    val deleteLocationResult = MutableLiveData<Evento<StatusAndMessageResponse>>()
    val oldPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val repeatNewPassword = MutableLiveData<String>()

    var sharingMode: MutableLiveData<SharingMode> = MutableLiveData(SharingMode.OFF)
    var scheduledSharing: MutableLiveData<ScheduledTime> = MutableLiveData(ScheduledTime(0,0))

    lateinit var startHourPicker: MutableLiveData<NumberPicker>
    lateinit var endHourPicker: MutableLiveData<NumberPicker>

    fun changePassword() {
        viewModelScope.launch{
            if (repeatNewPassword.value == newPassword.value){
                changePasswordResult.postValue(Evento(
                    dataRepository.apiChangePassword(
                        oldPassword.value?:"",
                        newPassword.value?:"")))
            }
            else {
                changePasswordResult.postValue(Evento(StatusAndMessageResponse("Error", "Passwords do not match")))
            }
            clearFormFields()
        }
    }

    private fun clearFormFields(){
        oldPassword.postValue("")
        newPassword.postValue("")
        repeatNewPassword.postValue("")
    }

    fun deleteUserLocation(){
        viewModelScope.launch {
            val result = dataRepository.apiDeleteUserLocation()
            deleteLocationResult.postValue(Evento(result))
        }
    }
}