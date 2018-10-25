package com.myetherwallet.mewconnect.core.ui.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by BArtWell on 15.08.2018.
 */
abstract class BaseRecyclerAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    internal fun inflate(@LayoutRes layoutRes: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }
}