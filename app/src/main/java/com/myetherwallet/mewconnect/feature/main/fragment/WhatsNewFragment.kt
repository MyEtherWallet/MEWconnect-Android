package com.myetherwallet.mewconnect.feature.main.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_whats_new.*
import java.util.regex.Pattern

/**
 * Created by BArtWell on 04.06.2019.
 */

class WhatsNewFragment : BaseDiFragment() {

    private val headerSize by lazy { resources.getDimension(R.dimen.text_size_18sp) }
    private val headerPaddingLeft by lazy { resources.getDimension(R.dimen.dimen_52dp).toInt() }
    private val headerPaddingTop by lazy { resources.getDimension(R.dimen.dimen_32dp).toInt() }
    private val bodySize by lazy { resources.getDimension(R.dimen.text_size_16sp) }
    private val bodyDashPaddingLeft by lazy { resources.getDimension(R.dimen.dimen_24dp).toInt() }
    private val bodyTextPaddingTop by lazy { resources.getDimension(R.dimen.dimen_12dp).toInt() }
    private val bodyTextPaddingLeft by lazy { resources.getDimension(R.dimen.dimen_16dp).toInt() }
    private val bodyLineSpacing by lazy { resources.getDimension(R.dimen.dimen_2dp) }

    companion object {
        fun newInstance() = WhatsNewFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        whats_new_toolbar.inflateMenu(R.menu.close)
        whats_new_toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
            close()
            true
        })

        val text = getString(R.string.whats_new)
        val pattern = Pattern.compile("### Release ([^(]+)\\(\\d+\\)(?:\n\n### [A-Za-z]+)?\n\n([^#]+)\n\n")
        val matcher = pattern.matcher(text)
        var isFirstIteration = true
        while (matcher.find()) {
            whats_new_content_container.addView(createHeader(matcher.group(1), isFirstIteration))
            val bodyText = matcher.group(2)
            if (bodyText.startsWith('-')) {
                val lines = bodyText.split('\n')
                for (line in lines) {
                    whats_new_content_container.addView(createBody(line))
                }
            } else {
                whats_new_content_container.addView(createBody(bodyText, false))
            }
            if (isFirstIteration) {
                isFirstIteration = false
            }
        }
    }

    private fun createHeader(value: String?, isFirst: Boolean) = TextView(requireContext()).apply {
        text = value
        setTypeface(null, Typeface.BOLD)
        val marginTop = if (isFirst) 0 else headerPaddingTop
        setPadding(headerPaddingLeft, marginTop, 0, 0)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, headerSize)
        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
        letterSpacing = 0.02f
    }

    private fun createBody(value: String?, showDash: Boolean = true): View {
        val linearLayout = LinearLayout(requireContext())
        linearLayout.setPadding(0, bodyTextPaddingTop, bodyTextPaddingTop, 0)

        val dashView = TextView(requireContext()).apply {
            text = "–"
            includeFontPadding = false
            setPadding(bodyDashPaddingLeft, 0, 0, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, bodySize)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
            letterSpacing = 0.02f
        }
        linearLayout.addView(dashView)
        if (!showDash) {
            dashView.visibility = View.INVISIBLE
        }

        val textView = TextView(requireContext()).apply {
            removeDash(value)?.let {
                text = StringUtils.fromHtml(StringUtils.replaceBreaks(it))
            }
            includeFontPadding = false
            setPadding(bodyTextPaddingLeft, 0, 0, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, bodySize)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
            letterSpacing = 0.02f
            setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bodyLineSpacing, resources.displayMetrics), 1.0f)
            movementMethod = LinkMovementMethod.getInstance()
        }
        linearLayout.addView(textView)
        return linearLayout
    }

    private fun removeDash(text: String?) = text?.removePrefix("- ")

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_whats_new
}