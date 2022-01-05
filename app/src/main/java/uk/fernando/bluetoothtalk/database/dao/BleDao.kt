package uk.fernando.bluetoothtalk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity

@Dao
interface BleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: UserEntity)


}