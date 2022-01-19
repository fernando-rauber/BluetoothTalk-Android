package uk.fernando.bluetoothtalk.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.fernando.bluetoothtalk.BaseApplication
import uk.fernando.bluetoothtalk.database.BleDatabase
import uk.fernando.bluetoothtalk.database.dao.BleDao
import uk.fernando.bluetoothtalk.repository.MessageRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): BleDatabase {
        return Room.databaseBuilder(context, BleDatabase::class.java, "blechat.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun reportDAO(db: BleDatabase): BleDao {
        return db.bleDao()
    }

    @Singleton
    @Provides
    fun provideMessageRepository(dao: BleDao) = MessageRepository(dao)

}