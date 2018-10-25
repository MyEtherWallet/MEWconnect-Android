package com.myetherwallet.mewconnect.core.persist.database

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * Created by BArtWell on 17.09.2018.
 */

class DateTypeConverter {

    @TypeConverter
    fun toDate(value: Long?) = value?.let { Date(it) }

    @TypeConverter
    fun toLong(value: Date?) = value?.time
}
