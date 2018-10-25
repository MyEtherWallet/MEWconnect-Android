package com.myetherwallet.mewconnect.core.persist.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.myetherwallet.mewconnect.feature.buy.data.BuyHistoryItem
import com.myetherwallet.mewconnect.feature.buy.database.BuyHistoryDao

/**
 * Created by BArtWell on 17.09.2018.
 */

private const val VERSION = 1

@Database(entities = [BuyHistoryItem::class], version = VERSION)
@TypeConverters(DateTypeConverter::class)
abstract class MewDatabase : RoomDatabase() {

    abstract fun getBuyHistoryDao(): BuyHistoryDao
}