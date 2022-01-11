package uk.fernando.bluetoothtalk.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.fernando.bluetoothtalk.database.converter.DateTypeConverter
import uk.fernando.bluetoothtalk.database.dao.BleDao
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.ProfileEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity


@TypeConverters(DateTypeConverter::class)
@Database(
    version = DATABASE_VERSION,
    exportSchema = false,
    entities = [UserEntity::class, ProfileEntity::class,
        MessageEntity::class]
)
abstract class BleDatabase : RoomDatabase() {

    abstract fun bleDao(): BleDao
}

const val DATABASE_VERSION = 1
