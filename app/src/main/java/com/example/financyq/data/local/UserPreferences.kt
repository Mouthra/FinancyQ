package com.example.financyq.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveToken(refreshToken: String){
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = refreshToken
        }
    }

    suspend fun clearToken(){
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun saveIdUser(idUser: String){
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = idUser
        }
    }

    suspend fun clearUserId(){
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }

    suspend fun saveIdtransactionincome(idTransaksiPemasukan: String){
        dataStore.edit { preferences ->
            preferences[ID_TRANSACTION_INCOME_KEY] = idTransaksiPemasukan
        }
    }

    suspend fun clearIdtansactionincome(){
        dataStore.edit { preferences ->
            preferences.remove(ID_TRANSACTION_INCOME_KEY)
        }
    }

    suspend fun saveIdtransactionexpenditure(idTransaksiPengeluaran: String){
        dataStore.edit { preferences ->
            preferences[ID_TRANSACTION_EXPENDITURE_KEY] = idTransaksiPengeluaran
        }
    }

    suspend fun clearIdtransactionexpenditure(){
        dataStore.edit { preferences ->
            preferences.remove(ID_TRANSACTION_EXPENDITURE_KEY)
        }
    }

    suspend fun saveUsername(username: String){
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun clearUsername(){
        dataStore.edit { preferences ->
            preferences.remove(USERNAME_KEY)
        }
    }

    val tokenFlow: Flow<String?> =dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userIdFlow: Flow<String?> =dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }


    val userNameFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }



    companion object{
        private val TOKEN_KEY = stringPreferencesKey("refreshToken")
        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val ID_TRANSACTION_INCOME_KEY = stringPreferencesKey("idTransaksiPemasukan")
        private val ID_TRANSACTION_EXPENDITURE_KEY = stringPreferencesKey("idTransaksiPengeluaran")
        private val USERNAME_KEY = stringPreferencesKey("username")

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: UserPreferences(context)
            }.also { INSTANCE = it }
        }
    }
}