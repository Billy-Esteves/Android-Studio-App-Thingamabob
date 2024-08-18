package com.example.myfirstworkingapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map


// Constant to define the name of the DataStore file
const val USER_DATASTORE = "user_data"

// Extension property to create the DataStore instance in the Context
val Context.preferenceDataStore : DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

// Class to manage the DataStore operations
class DataStoreManager(val context : Context) {

    // Gson instance for serializing and deserializing the list of notes
    private val gson = Gson()

    // Companion object to hold the keys for storing data in DataStore
    companion object{
        val STORED_NOTES = stringPreferencesKey("notes")
    }

    /**
     * Saves a list of Note objects to the DataStore.
     * @param notes The list of Note objects to be saved.
     */
    suspend fun saveTodDataStore(notes: List<Note>){
        // Convert the list of notes into a JSON STRING
        val notesJson = gson.toJson(notes)

        // Save the JSON string to the DataStore using the STORED_NOTES key
        context.preferenceDataStore.edit { preferences ->
            preferences[STORED_NOTES] = notesJson
        }
    }

    /**
     * Retrieves the list of Note objects from the DataStore.
     * @return A Flow that emits the list of Note objects retrieved from the DataStore.
     */
    fun getFromDataStore() = context.preferenceDataStore.data.map { preferences ->

        // Retrieve the JSON string from the DataStore using the STORED_NOTES key
        // If no data is found, return an empty JSON array string ("[]")
        val notesJson = preferences[STORED_NOTES] ?: "[]"

        // Define the type of the data we expect to deserialize (List<Note>)
        val type = object : TypeToken<List<Note>>() {}.type

        // Deserialize the JSON string back into a list of Note objects
        gson.fromJson<List<Note>>(notesJson, type)
    }

    /**
     * Clears all data from the DataStore.
     */
    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }

}