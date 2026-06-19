package com.guptarajat.screenactivetaskreminder.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val AUTH_STORE_NAME = "task_reminder_auth"

private val Context.taskReminderAuthDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AUTH_STORE_NAME,
)

class AuthStore(context: Context) {
    private val dataStore = context.applicationContext.taskReminderAuthDataStore

    val session: Flow<AuthSession> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            AuthSession(
                accountId = preferences[AccountIdKey],
                email = preferences[EmailKey],
                displayName = preferences[DisplayNameKey],
                profilePictureUri = preferences[ProfilePictureUriKey],
            )
        }

    suspend fun saveSession(session: AuthSession) {
        dataStore.edit { preferences ->
            putOrRemove(preferences, AccountIdKey, session.accountId)
            putOrRemove(preferences, EmailKey, session.email)
            putOrRemove(preferences, DisplayNameKey, session.displayName)
            putOrRemove(preferences, ProfilePictureUriKey, session.profilePictureUri)
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun putOrRemove(
        preferences: MutablePreferences,
        key: Preferences.Key<String>,
        value: String?,
    ) {
        if (value.isNullOrBlank()) {
            preferences.remove(key)
        } else {
            preferences[key] = value
        }
    }

    private companion object {
        val AccountIdKey = stringPreferencesKey("account_id")
        val EmailKey = stringPreferencesKey("email")
        val DisplayNameKey = stringPreferencesKey("display_name")
        val ProfilePictureUriKey = stringPreferencesKey("profile_picture_uri")
    }
}

private typealias MutablePreferences = androidx.datastore.preferences.core.MutablePreferences
