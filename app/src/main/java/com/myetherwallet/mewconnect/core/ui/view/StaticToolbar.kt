package com.myetherwallet.mewconnect.core.ui.view

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.MenuRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.CardBackgroundHelper
import kotlinx.android.synthetic.main.view_static_toolbar.view.*
import javax.inject.Inject

private const val NAMESPACE = "http://schemas.android.com/apk/res/android"
private const val ATTRIBUTE_MARGIN = "dividerPadding"
private const val ATTRIBUTE_BACKGROUND = "panelBackground"

class StaticToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var preferences: PreferencesManager

    private var margin = resources.getDimension(R.dimen.dimen_32dp).toInt()
    private var backgroundResId = -1

    init {
        View.inflate(context, R.layout.view_static_toolbar, this)
        attrs?.let {
            val resId = it.getAttributeResourceValue(NAMESPACE, ATTRIBUTE_MARGIN, 0)
            if (resId != 0) {
                margin = resources.getDimension(resId).toInt()
            }
            backgroundResId = it.getAttributeResourceValue(NAMESPACE, ATTRIBUTE_BACKGROUND, -1)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) {
            return
        }

        (context.applicationContext as MewApplication).appComponent.inject(this)

        val marginLayoutParams = static_toolbar_bar.layoutParams as MarginLayoutParams
        marginLayoutParams.topMargin = margin
        static_toolbar_bar.layoutParams = marginLayoutParams
        val network = preferences.applicationPreferences.getCurrentNetwork()
        if (backgroundResId == 0) {
            static_toolbar_background.setImageDrawable(null)
        } else if (backgroundResId != -1) {
            static_toolbar_background.setImageDrawable(ContextCompat.getDrawable(context, backgroundResId))
        } else if (CardBackgroundHelper.isExists(context, network)) {
            CardBackgroundHelper.setImage(static_toolbar_background, network)
        }
    }

    fun setBackgroundImageResource(@DrawableRes resourceId: Int) {
        static_toolbar_background.setImageResource(resourceId)
    }

    fun setDrawable(@DrawableRes toolbarDrawable: Int) {
        static_toolbar_bar.setBackgroundResource(toolbarDrawable)
    }

    fun setTitle(@StringRes stringResource: Int) {
        static_toolbar_bar.setTitle(stringResource)
    }

    fun setTitle(title: String) {
        static_toolbar_bar.title = title
    }

    fun inflateMenu(@MenuRes menu: Int) {
        static_toolbar_bar.inflateMenu(menu)
    }

    fun getMenu(): Menu = static_toolbar_bar.menu

    fun setOnMenuItemClickListener(listener: Toolbar.OnMenuItemClickListener) {
        static_toolbar_bar.setOnMenuItemClickListener(listener)
    }

    fun setNavigationIcon(@DrawableRes icon: Int) {
        static_toolbar_bar.setNavigationIcon(icon)

    }

    fun setNavigationOnClickListener(listener: OnClickListener) {
        static_toolbar_bar.setNavigationOnClickListener(listener)
    }
}