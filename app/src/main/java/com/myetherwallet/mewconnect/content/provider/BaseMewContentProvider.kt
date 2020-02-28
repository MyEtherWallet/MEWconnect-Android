package com.myetherwallet.mewconnect.content.provider

import android.content.ContentProvider
import android.database.Cursor
import android.database.MatrixCursor

/**
 * Created by BArtWell on 27.02.2020.
 */

abstract class BaseMewContentProvider : ContentProvider() {

    protected fun <T> createOneItemCursor(data: T): Cursor {
        val cursor = MatrixCursor(arrayOf("_id", "data"))
        cursor.newRow()
                .add(0)
                .add(data)
        return cursor
    }
}
