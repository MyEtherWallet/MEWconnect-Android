package com.myetherwallet.mewconnect.feature.buy.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.dialog.ConfirmDialog
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.feature.scan.utils.PermissionHelper
import kotlinx.android.synthetic.main.activity_buy_webview.*

private const val TAG = "BuyWebViewActivity"
private const val EXTRA_URL = "url"
private const val EXTRA_POST_DATA = "post_data"
private const val REQUEST_CODE_UPLOAD = 1
private const val REQUEST_CODE_SETTINGS = 2

class BuyWebViewActivity : AppCompatActivity() {

    private val permissionHelper = PermissionHelper(android.Manifest.permission.READ_EXTERNAL_STORAGE, ::handleUpload)
    private var chooserParams: Pair<ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams>? = null

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
        buy_webview.webChromeClient = MewChromeClient()
        buy_webview.settings.javaScriptEnabled = true

        val url = intent.getStringExtra(EXTRA_URL)
        val postData = intent.getStringExtra(EXTRA_POST_DATA)
        buy_webview.postUrl(url, postData.toByteArray())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_UPLOAD) {
            MewLog.d(TAG, "Handling chooser result")
            if (resultCode == Activity.RESULT_OK) {
                MewLog.d(TAG, "Set chooser result")
                chooserParams?.first?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            } else {
                chooserParams?.first?.onReceiveValue(null)
            }
            chooserParams = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun handleUpload(isGranted: Boolean) {
        MewLog.d(TAG, "handleUpload")
        if (isGranted) {
            MewLog.d(TAG, "Granted")
            chooserParams?.let {
                try {
                    startActivityForResult(it.second.createIntent(), REQUEST_CODE_UPLOAD)
                } catch (e: ActivityNotFoundException) {
                    MewLog.e(TAG, "Unable to open file chooser", e)
                    Toast.makeText(this@BuyWebViewActivity, R.string.buy_webview_upload_error, Toast.LENGTH_LONG).show()
                    chooserParams?.first?.onReceiveValue(null)
                    chooserParams = null
                }
            }
        } else {
            MewLog.d(TAG, "Not granted")
            chooserParams?.first?.onReceiveValue(null)
            chooserParams = null
            if (!permissionHelper.shouldShowRequestPermissionsRationale(this)) {
                val dialog = ConfirmDialog.newInstance(getString(R.string.buy_webview_rationale))
                dialog.listener = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_CODE_SETTINGS)
                }
                dialog.show(supportFragmentManager)
            }
        }
    }

    private inner class MewChromeClient : WebChromeClient() {
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            chooserParams = Pair(filePathCallback, fileChooserParams)
            permissionHelper.requestPermissions(this@BuyWebViewActivity)
            return true
        }
    }
}
