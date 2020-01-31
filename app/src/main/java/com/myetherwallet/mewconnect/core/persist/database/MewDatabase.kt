package com.myetherwallet.mewconnect.core.persist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myetherwallet.mewconnect.feature.buy.data.BuyHistoryItem
import com.myetherwallet.mewconnect.feature.buy.database.BuyHistoryDao

/**
 * Created by BArtWell on 17.09.2018.
 */

private const val VERSION = 2

@Database(entities = [BuyHistoryItem::class], version = VERSION)
@TypeConverters(DateTypeConverter::class)
abstract class MewDatabase : RoomDatabase() {

    abstract fun getBuyHistoryDao(): BuyHistoryDao
}