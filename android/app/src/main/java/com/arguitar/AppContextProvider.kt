package com.arguitar

import android.content.Context

object AppContextProvider {
    private lateinit var context: Context

    // Initialize the context
    fun init(context: Context) {
        this.context = context.applicationContext
    }

    // Get the application context
    fun getContext(): Context {
        return context
    }
}