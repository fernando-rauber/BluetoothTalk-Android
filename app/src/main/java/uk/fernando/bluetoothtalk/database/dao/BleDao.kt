package uk.fernando.bluetoothtalk.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.database.entity.UserWithChat

@Dao
interface BleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: UserEntity)

    @Transaction
    @Query("SELECT * FROM ${UserEntity.NAME} ")
    fun getAllUserWithChat(): Flow<List<UserWithChat>>
}