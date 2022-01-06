package uk.fernando.bluetoothtalk.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.database.entity.UserWithMessage

@Dao
interface BleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: MessageEntity)

    @Query("SELECT * FROM ${UserEntity.NAME} WHERE address = :address")
    fun getUserById(address: String): UserEntity

    @Query("SELECT * FROM ${MessageEntity.NAME} WHERE user_address = :address ORDER BY date ASC")
    fun getMessagesByUser(address: String): Flow<List<MessageEntity>>

    @Transaction
    @Query("SELECT * FROM ${UserEntity.NAME} ")
    fun getAllUserWithMessage(): Flow<List<UserWithMessage>>
}