package com.myetherwallet.mewconnect.core.persist.database

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 17.09.2018.
 */

private const val DATABASE_NAME = "mew_db.sqlite"

@Module
class DatabaseModule @Inject constructor(private val context: Context) {

    @Singleton
    @Provides
    fun provideDatabase(): MewDatabase {
        return Room.databaseBuilder(context, MewDatabase::class.java, DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    internal fun provideBuyHistoryDao(database: MewDatabase) = database.getBuyHistoryDao()
}
