package com.example.mobv.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobv.data.DataRepository
import com.example.mobv.data.models.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<Pair<String, User?>>()
    val registrationResult: LiveData<Pair<String, User?>> get() = _registrationResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationResult.postValue(dataRepository.apiRegisterUser(username, email, password))
        }
    }
}