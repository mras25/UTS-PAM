package com.itera.utspam.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.itera.utspam.data.model.LocalUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: LocalUser) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_GITHUB_KEY] = user.githubUsername
        }
    }

    fun getSession(): Flow<LocalUser> {
        return dataStore.data.map { preferences ->
            LocalUser(
                name = preferences[USER_NAME_KEY] ?: "",
                email = preferences[USER_EMAIL_KEY] ?: "",
                githubUsername = preferences[USER_GITHUB_KEY] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val USER_NAME_KEY = stringPreferencesKey("userName")
        private val USER_EMAIL_KEY = stringPreferencesKey("userEmail")
        private val USER_GITHUB_KEY = stringPreferencesKey("userGithub")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}