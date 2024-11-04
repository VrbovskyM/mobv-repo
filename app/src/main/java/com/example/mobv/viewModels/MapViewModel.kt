package com.example.mobv.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobv.data.DataRepository
import kotlinx.coroutines.launch

class MapViewModel (private val repository: DataRepository) : ViewModel() {

    var updatedLocationResult = MutableLiveData<String>()

    fun updateLocation(lat: Double, lon: Double, radius: Double) {
        viewModelScope.launch {
            var result = repository.apiUpdateUserLocation(lat, lon, radius)
            updatedLocationResult.postValue(result.status)
        }
    }
}