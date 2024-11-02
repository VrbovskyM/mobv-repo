package com.example.mobv.viewModels

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

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    val changePasswordResult = MutableLiveData<StatusAndMessageResponse>()
    val resetPasswordResult = MutableLiveData<Evento<StatusAndMessageResponse>>()
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(username, email, password)
            _registrationResult.postValue(result)

            if (result.second != null) {
                loadAndUpdateUserInPreference(result.second!!.id)
            }
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username.value?:"", password.value?:"")
            _loginResult.postValue(result)

            if (result.second != null) {
                loadAndUpdateUserInPreference(result.second!!.id)
            }
        }
    }
    private suspend fun loadAndUpdateUserInPreference(userId: String): Boolean {
        val result = dataRepository.apiGetUser(userId)
        if (result.second != null) {
            val isUpdated = PreferenceData.getInstance().updateUser(result.second!!)
            return isUpdated
        }
        return false
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

    fun resetPassword() {
        viewModelScope.launch {
            resetPasswordResult.postValue(Evento(dataRepository.apiResetPassword(email.value?:"")))
        }
    }
}
