package com.example.mobv.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobv.data.DataRepository
import com.example.mobv.data.models.User
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel(){

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun changePassword() {
        TODO("Not yet implemented")
    }

//    fun loadUserProfile() {
//        viewModelScope.launch {
//            try {
//                _user.postValue()
//            } catch (e: Exception) {
//                // Handle error
//            }
//        }
//    }
}