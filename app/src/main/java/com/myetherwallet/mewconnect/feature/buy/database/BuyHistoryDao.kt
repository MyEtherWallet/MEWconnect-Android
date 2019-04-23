package com.myetherwallet.mewconnect.feature.buy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.myetherwallet.mewconnect.feature.buy.data.BuyHistoryItem

/**
 * Created by BArtWell on 17.09.2018.
 */

@Dao
interface BuyHistoryDao {

    @Insert(onConflict = REPLACE)
    fun insert(item: BuyHistoryItem)

    @Query("SELECT * FROM buy_history ORDER BY id DESC")
    abstract fun getAll(): List<BuyHistoryItem>
}
