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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseApplication {
        return app as BaseApplication
    }

}