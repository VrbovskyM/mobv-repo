package com.example.mobv.viewModels

import android.util.Log
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
    private val _authResult = MutableLiveData<Pair<String, User?>>()
    val authResult: LiveData<Pair<String, User?>> get() = _authResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    val resetPasswordResult = MutableLiveData<Evento<StatusAndMessageResponse>>()
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeatPassword = MutableLiveData<String>()

    fun registerUser() {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(username.value?:"", email.value?:"", password.value?:"")
            _authResult.postValue(result)

            if (result.second != null) {
                loadAndUpdateUserInPreference(result.second!!.id)
            }
            clearFormFields()
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(username.value?:"", password.value?:"")
            _authResult.postValue(result)

            if (result.second != null) {
                loadAndUpdateUserInPreference(result.second!!.id)
            }
            clearFormFields()
        }
    }
    private suspend fun loadAndUpdateUserInPreference(userId: String): Boolean {
        val result = dataRepository.apiGetUser(userId)
        val user = PreferenceData.getInstance().getUser()
        Log.d("AuthViewModel", "User updated: $user")
        if (result.second != null) {
            val isUpdated = PreferenceData.getInstance().updateUser(result.second!!)
            val user2 = PreferenceData.getInstance().getUser()
            Log.d("AuthViewModel", "User updated: $user2")
            return isUpdated
        }
        return false
    }

    fun logout() {
        viewModelScope.launch {
            _authResult.postValue(Pair("Logging out", null))
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            resetPasswordResult.postValue(Evento(dataRepository.apiResetPassword(email.value?:"")))
        }
    }
    private fun clearFormFields(){
        username.postValue("")
        email.postValue("")
        password.postValue("")
        repeatPassword.postValue("")
    }
}
