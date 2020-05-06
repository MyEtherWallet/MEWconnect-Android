package com.myetherwallet.mewconnect.feature.main.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlinx.android.synthetic.main.view_wallet_mewwallet.view.*
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Created by BArtWell on 29.01.2020.
 */

private val ANIMATION_REPEAT_DELAY = TimeUnit.SECONDS.toMillis(30)

class MewWalletView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.cardview.widget.CardView(context, attrs, defStyleAttr), WalletScrollable {

    var clickListener: (() -> Unit)? = null
    private var staticPosition = false
    private var text: String?
    private var buttonText: String?
    private val viewHandler = Handler()

    init {
        View.inflate(context, R.layout.view_wallet_mewwallet, this)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MewWalletView)
        staticPosition = typedArray.getBoolean(R.styleable.MewWalletView_staticPosition, false)
        text = typedArray.getString(R.styleable.MewWalletView_text)
        buttonText = typedArray.getString(R.styleable.MewWalletView_buttonText)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (!staticPosition) {
            val statusBarHeight = ApplicationUtils.getStatusBarHeight(context)
            val toolbarHeight = statusBarHeight + resources.getDimension(R.dimen.wallet_toolbar_height).toInt()
            y = toolbarHeight + WalletSizingUtils.calculateCardHeight() + resources.getDimension(R.dimen.dimen_16dp)
        }

        wallet_mewwallet_button.setOnClickListener {
            if (staticPosition || alpha > 0.90f) {
                clickListener?.invoke()
            }
        }

        playAnimation()

        text?.let { wallet_mewwallet_text.text = it }
        buttonText?.let { wallet_mewwallet_button.text = it }
    }

    private fun playAnimation() {
        wallet_mewwallet_animation.playAnimation()
        viewHandler.postDelayed(::playAnimation, ANIMATION_REPEAT_DELAY)
    }

    override fun setRatio(ratio: Float) {
        val newRatio = max(ratio - 0.8f, 0f) / 0.2f
        alpha = newRatio
    }
}
