package uk.fernando.bluetoothtalk.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.fernando.bluetoothtalk.database.dao.BleDao
import uk.fernando.bluetoothtalk.database.entity.MessageEntity

class MessageRepository(private val dao: BleDao) {

    suspend fun insertMessage(message: MessageEntity) = withContext(Dispatchers.IO) {
        dao.insert(message)
    }

    suspend fun updateMessageToSent(messageID: Long) {
        withContext(Dispatchers.IO) {
            dao.updateMessageToSent(messageID)
        }
    }

    suspend fun getMessagesByUser(address: String) = withContext(Dispatchers.IO) {
        dao.getMessagesByUser(address)
    }

}
