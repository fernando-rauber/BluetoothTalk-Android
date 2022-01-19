package uk.fernando.bluetoothtalk.repository

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.fernando.bluetoothtalk.database.dao.BleDao
import uk.fernando.bluetoothtalk.database.entity.ProfileEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.getRandomUUIDString

class UserRepository(private val dao: BleDao) {

    suspend fun insertUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.insert(user)
        }
    }

    suspend fun updateProfile(user: ProfileEntity) {
        withContext(Dispatchers.IO) {
            dao.update(user)
        }
    }

    suspend fun getProfile() = withContext(Dispatchers.IO) {
        val profile = dao.getProfile()
        if (profile != null)
            profile
        else {
            val newProfile = ProfileEntity(getRandomUUIDString(), Build.MODEL)
            dao.insert(newProfile)
            newProfile
        }
    }

    suspend fun getUserById(address: String) = withContext(Dispatchers.IO) {
        dao.getUserById(address)
    }

    suspend fun getAllUserWithMessage() = withContext(Dispatchers.IO) {
        dao.getAllUserWithMessage()
    }

}
