package com.myetherwallet.mewconnect.feature.scan.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_transaction_declined.*
import kotlinx.android.synthetic.main.fragment_transaction_declined.view.*

/**
 * Created by BArtWell on 11.07.2018.
 */

class TransactionDeclinedFragment : BaseDiFragment() {

    companion object {

        fun newInstance(): TransactionDeclinedFragment = TransactionDeclinedFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHtmlWithLinks(view.transaction_declined_description, R.string.transaction_declined_description)
        transaction_declined_close.setOnClickListener { close() }
    }

    private fun setHtmlWithLinks(textView: TextView, strRes: Int) {
        val spannable = StringUtils.fromHtml(getString(strRes)) as Spannable
        for (urlSpan in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
            spannable.setSpan(object : UnderlineSpan() {
                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.isUnderlineText = false
                }
            }, spannable.getSpanStart(urlSpan), spannable.getSpanEnd(urlSpan), 0)
        }
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_transaction_declined
}