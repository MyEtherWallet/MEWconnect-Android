package com.myetherwallet.mewconnect.core.ui.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.Toolbar
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.core.utils.CardBackgroundHelper
import kotlinx.android.synthetic.main.view_static_toolbar.view.*
import javax.inject.Inject

class StaticToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var preferences: PreferencesManager

    private val toolbarMargin = ApplicationUtils.getToolbarMargin(this)
    private val toolbarHeight = resources.getDimension(R.dimen.dimen_56dp).toInt()
    private var topMargin = toolbarMargin + resources.getDimension(R.dimen.dimen_8dp).toInt()
    private var bottomMargin = 0
    private var backgroundResId = -1

    init {
        View.inflate(context, R.layout.view_static_toolbar, this)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StaticToolbar)
        val innerTopPadding = typedArray.getDimension(R.styleable.StaticToolbar_innerTopPadding, -1f).toInt()
        if (innerTopPadding != -1) {
            topMargin = toolbarMargin + innerTopPadding
        }
        val innerBottomPadding = typedArray.getDimension(R.styleable.StaticToolbar_innerBottomPadding, -1f).toInt()
        if (innerBottomPadding != -1) {
            bottomMargin = innerBottomPadding
        }
        backgroundResId = typedArray.getResourceId(R.styleable.StaticToolbar_innerBackground, -1)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) {
            return
        }

        (context.applicationContext as MewApplication).appComponent.inject(this)
        setToolbarMargin()
        setViewHeight()

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

    private fun setToolbarMargin() {
        val marginLayoutParams = static_toolbar_bar.layoutParams as MarginLayoutParams
        marginLayoutParams.topMargin = topMargin
        marginLayoutParams.height += bottomMargin
        static_toolbar_bar.layoutParams = marginLayoutParams
    }

    private fun setViewHeight() {
        val layoutParams = static_toolbar_container.layoutParams
        layoutParams.height = toolbarHeight + topMargin + bottomMargin
        static_toolbar_container.layoutParams = layoutParams
    }
}