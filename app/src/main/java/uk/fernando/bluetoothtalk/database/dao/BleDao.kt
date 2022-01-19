package uk.fernando.bluetoothtalk.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.ProfileEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.database.entity.UserWithMessage

@Dao
interface BleDao {

    //region USER
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: UserEntity)

    @Query("SELECT * FROM ${UserEntity.NAME} WHERE id = :id")
    fun getUserById(id: String): UserEntity

    //endregion


    //region Message
    @Insert
    fun insert(item: MessageEntity): Long

    @Query("SELECT * FROM ${MessageEntity.NAME} WHERE user_id = :userId ORDER BY date ASC")
    fun getMessagesByUser(userId: String): Flow<List<MessageEntity>>

    @Transaction
    @Query("SELECT * FROM ${UserEntity.NAME} ")
    fun getAllUserWithMessage(): Flow<List<UserWithMessage>>

    @Query("UPDATE ${MessageEntity.NAME} SET sent = 1 WHERE id =:id")
    fun updateMessageToSent(id: Long)

    //endregion

    //region Profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ProfileEntity)

    @Update
    fun update(item: ProfileEntity)

    @Query("SELECT * FROM ${ProfileEntity.NAME} LIMIT 1")
    fun getProfile(): ProfileEntity?

    //endregion
}