package com.example.myfirstworkingapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map




const val USER_DATASTORE = "user_data"

val Context.preferenceDataStore : DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreManager(val context : Context) {

    companion object{
        val STORED_STRING = stringSetPreferencesKey("noteString")
        val STORED_CHECKED_STATUS = booleanPreferencesKey("checkedStatus")
    }

    suspend fun saveTodDataStore(note: Note){
        context.preferenceDataStore.edit {
            it[STORED_STRING] = setOf(note.text)
            it[STORED_CHECKED_STATUS] = note.checked
        }
    }

    fun getFromDataStore() = context.preferenceDataStore.data.map {
        Note(
            text = it[STORED_STRING].toString(),
            checked = it[STORED_CHECKED_STATUS] == true
        )
    }

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }

}