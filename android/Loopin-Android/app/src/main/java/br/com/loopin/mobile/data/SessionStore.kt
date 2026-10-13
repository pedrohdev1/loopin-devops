package br.com.loopin.mobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.sessionDataStore by preferencesDataStore(name = "loopin_session")

private val TOKEN_KEY = stringPreferencesKey("jwt_token")
private val ID_KEY = stringPreferencesKey("user_id")
private val NAME_KEY = stringPreferencesKey("user_name")
private val EMAIL_KEY = stringPreferencesKey("user_email")
private val ROLE_KEY = stringPreferencesKey("user_role")

data class UserSession(
    val token: String,
    val id: String,
    val nome: String,
    val email: String,
    val perfil: String
)

class SessionStore(context: Context) {
    private val store = context.applicationContext.sessionDataStore

    val sessions: Flow<UserSession?> = store.data
        .catch { cause ->
            if (cause is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw cause
        }
        .map { prefs ->
            val token = prefs[TOKEN_KEY]
            if (token.isNullOrBlank()) null else UserSession(
                token = token,
                id = prefs[ID_KEY].orEmpty(),
                nome = prefs[NAME_KEY].orEmpty(),
                email = prefs[EMAIL_KEY].orEmpty(),
                perfil = prefs[ROLE_KEY].orEmpty().uppercase()
            )
        }

    suspend fun save(response: LoginResponse) {
        TokenHolder.accessToken = response.token
        store.edit { prefs ->
            prefs[TOKEN_KEY] = response.token
            prefs[ID_KEY] = response.id
            prefs[NAME_KEY] = response.nome
            prefs[EMAIL_KEY] = response.email
            prefs[ROLE_KEY] = response.perfil
        }
    }

    suspend fun clear() {
        TokenHolder.accessToken = null
        store.edit { it.clear() }
    }
}
