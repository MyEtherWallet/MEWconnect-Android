package com.myetherwallet.mewconnect.feature.buy.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebViewClient
import com.myetherwallet.mewconnect.R
import kotlinx.android.synthetic.main.activity_buy_webview.*


private const val EXTRA_URL = "url"
private const val EXTRA_POST_DATA = "post_data"

class BuyWebViewActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context, url: String, postData: String): Intent {
            val intent = Intent(context, BuyWebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_POST_DATA, postData)
            return intent
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_webview)

        buy_webview_toolbar.title = getString(R.string.buy_title)
        buy_webview_toolbar.inflateMenu(R.menu.close)
        buy_webview_toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.close) {
                finish()
                true
            } else {
                false
            }
        }

        buy_webview.webViewClient = WebViewClient()
        buy_webview.settings.javaScriptEnabled = true

        val url = intent.getStringExtra(EXTRA_URL)
        val postData = intent.getStringExtra(EXTRA_POST_DATA)
        buy_webview.postUrl(url, postData.toByteArray())
    }
}
