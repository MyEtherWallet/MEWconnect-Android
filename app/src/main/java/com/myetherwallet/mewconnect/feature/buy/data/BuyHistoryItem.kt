package com.myetherwallet.mewconnect.feature.buy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


/**
 * Created by BArtWell on 17.09.2018.
 */

@Entity(tableName = "buy_history")
data class BuyHistoryItem(
        val userId: String,
        val quoteId: String,
        val date: Date = Date()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}