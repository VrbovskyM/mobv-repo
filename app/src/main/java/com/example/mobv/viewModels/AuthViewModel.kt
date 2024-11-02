package com.example.mobv.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobv.data.DataRepository
import com.example.mobv.data.PreferenceData
import com.example.mobv.data.models.User
import com.example.mobv.data.services.StatusAndMessageResponse
import com.example.mobv.utils.Evento
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<Pair<String, User?>>()
    val registrationResult: LiveData<Pair<String, User?>> get() = _registrationResult

    private val _loginResult = MutableLiveData<Pair<String, User?>>()
    val loginResult: LiveData<Pair<String, User?>> get() = _loginResult

    val changePasswordResult = MutableLiveData<StatusAndMessageResponse>()

    val resetPasswordResult = MutableLiveData<Evento<StatusAndMessageResponse>>()

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationResult.postValue(dataRepository.apiRegisterUser(username, email, password))
        }
    }

    fun loginUser(name: String, password: String) {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(name, password)
            _loginResult.postValue(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loginResult.postValue(Pair("Logging out", null))
            _registrationResult.postValue(Pair("Logging out", null))
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch{
            val result = dataRepository.apiChangePassword(oldPassword, newPassword)
            changePasswordResult.postValue(result)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            resetPasswordResult.postValue(Evento(dataRepository.apiResetPassword(email)))
        }
    }
}
