package com.example.mobv.data.localDb

import androidx.lifecycle.LiveData
import com.example.mobv.data.localDb.entities.UserEntity

class LocalCache(private val dao: Dao) {

    suspend fun logoutUser() {
        deleteUserItems()
    }

    suspend fun insertUserItems(items: List<UserEntity>) {
        dao.insertUserItems(items)
    }

    fun getUserItem(uid: String): LiveData<UserEntity?> {
        return dao.getUserItem(uid)
    }

    suspend fun getUsers(): LiveData<List<UserEntity>?> = dao.getUsers()

    suspend fun deleteUserItems() {
        dao.deleteUserItems()
    }

}
