package com.example.mobv

import android.app.Application
import android.content.Context


class MyApplication : Application() {
    private lateinit var appContext: Context  // Non-nullable and private

    companion object {
        private lateinit var instance: MyApplication

        fun getContext(): Context {
            return instance.appContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext
    }
}