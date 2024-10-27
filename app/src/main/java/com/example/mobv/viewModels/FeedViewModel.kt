package com.example.mobv.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.mobv.data.DataRepository
import com.example.mobv.data.db.entities.UserEntity
import com.example.mobv.utils.Evento
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: DataRepository) : ViewModel() {

    val feed_items: LiveData<List<UserEntity>?> =
        liveData {
            loading.postValue(true)
            repository.apiGeofenceUsers()
            loading.postValue(false)
            emitSource(repository.getUsers())
        }

    val loading = MutableLiveData(false)

    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    fun updateItems() {
        viewModelScope.launch {
            loading.postValue(true)
            _message.postValue(Evento(repository.apiGeofenceUsers()))
            loading.postValue(false)
        }
    }
}