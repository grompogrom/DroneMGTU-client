package com.pogrom.myapplication.tools

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

fun readIpFromDataStore(context: Context): Flow<String> {
    val IP_KEY = stringPreferencesKey("ip")
    return context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[IP_KEY] ?: ""
        }
}

suspend fun writeIpToDataStore(ip: String, context: Context){
    val IP_KEY = stringPreferencesKey("ip")
    context.dataStore.edit {settings ->
        settings[IP_KEY] = ip
    }
}