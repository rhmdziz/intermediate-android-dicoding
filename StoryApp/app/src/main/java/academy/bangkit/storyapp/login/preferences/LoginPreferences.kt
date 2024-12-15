package academy.bangkit.storyapp.login.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_preferences")

class LoginPreferences private constructor(private val dataStore: DataStore<Preferences>){

    // Fungsi untuk menyimpan status login dan token
    suspend fun saveSession(isLoggedIn: Boolean, token: String?) {
        dataStore.edit { preferences ->
            preferences[LoginPreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            preferences[LoginPreferencesKeys.TOKEN] = token ?: ""
        }
    }
    // Fungsi untuk mengambil status login
    fun getLoginStatus(): Flow<Boolean> {
        return dataStore.data
            .map { preferences ->
                preferences[LoginPreferencesKeys.IS_LOGGED_IN] ?: false
            }
    }
    // Fungsi untuk mengambil token
    fun getToken(): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[LoginPreferencesKeys.TOKEN]
            }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(LoginPreferencesKeys.IS_LOGGED_IN)
            preferences.remove(LoginPreferencesKeys.TOKEN)
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: LoginPreferences? = null

        fun getInstance(context: Context): LoginPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = LoginPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}