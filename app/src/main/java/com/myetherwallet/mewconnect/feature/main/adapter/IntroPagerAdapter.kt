package com.myetherwallet.mewconnect.feature.main.adapter

import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import kotlinx.android.synthetic.main.layout_intro_page_mew_connect.view.*

class IntroPagerAdapter : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val layout = createView(collection, Item.values()[position])
        collection.addView(layout)
        return layout
    }

    private fun createView(parent: ViewGroup, item: Item): View {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_intro_page_mew_connect, parent, false) as ViewGroup
        view.intro_footer_text.setText(item.footerText)
        view.intro_header_container.removeAllViews()
        if (item.headerResId != 0) {
            view.intro_header_container.addView(inflater.inflate(R.layout.layout_intro_page_header, view, false))
        }
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return Item.values().size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }

    private enum class Item(@StringRes val footerText: Int, @LayoutRes val headerResId: Int = 0) {
        MEW_CONNECT(R.string.intro_page_footer1, R.layout.layout_intro_page_header),
        MEW_CONNECT2(R.string.intro_page_footer2),
        MEW_CONNECT3(R.string.intro_page_footer3),
        MEW_CONNECT4(R.string.intro_page_footer4),
        MEW_CONNECT5(R.string.intro_page_footer5),
        MEW_CONNECT6(R.string.intro_page_footer6),
        MEW_CONNECT7(R.string.intro_page_footer7)
    }
}