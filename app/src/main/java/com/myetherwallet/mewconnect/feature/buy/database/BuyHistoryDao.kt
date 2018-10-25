package com.myetherwallet.mewconnect.feature.buy.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
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
