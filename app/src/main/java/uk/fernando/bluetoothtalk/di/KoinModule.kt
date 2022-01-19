package uk.fernando.bluetoothtalk.di


import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import uk.fernando.bluetoothtalk.database.BleDatabase
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.viewmodel.BluetoothViewModel
import uk.fernando.bluetoothtalk.viewmodel.ChatListViewModel
import uk.fernando.bluetoothtalk.viewmodel.ChatViewModel

object KoinModule {

    /**
     * Keep the order applied
     * @return List<Module>
     */
    fun allModules(): List<Module> =
        listOf(databaseModule, repositoryModule, viewModelModule)


    private val databaseModule = module {

        fun provideDatabase(application: Application): BleDatabase {
            return Room.databaseBuilder(application, BleDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        single { provideDatabase(androidApplication()) }
        factory { get<BleDatabase>().bleDao() }
    }

    private val repositoryModule: Module
        get() = module {

            factory { MessageRepository(get()) }

        }

    private val viewModelModule: Module
        get() = module {

            viewModel { BluetoothViewModel(androidApplication()) }
            viewModel { ChatViewModel(get()) }
            viewModel { ChatListViewModel(get()) }

        }

    private const val DB_NAME = "blechat.db"
}


