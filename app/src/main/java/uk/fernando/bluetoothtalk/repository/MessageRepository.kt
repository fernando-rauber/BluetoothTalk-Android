package uk.fernando.bluetoothtalk.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.fernando.bluetoothtalk.database.dao.BleDao
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import javax.inject.Inject

class MessageRepository @Inject constructor(private val dao: BleDao) {


    suspend fun insertMessage(message: MessageEntity) {
        withContext(Dispatchers.IO) {
//            dao.insert(speedReport)
        }
    }

    suspend fun insertUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            dao.insert(user)
        }
    }

    suspend fun getAllUserWithChat() = withContext(Dispatchers.IO) {
        dao.getAllUserWithChat()
    }

}
