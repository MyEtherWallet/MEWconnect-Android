package com.myetherwallet.mewconnect.feature.scan.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.core.utils.LaunchUtils
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.StringUtils
import com.myetherwallet.mewconnect.feature.main.activity.MainActivity
import com.myetherwallet.mewconnect.feature.scan.utils.BarcodeTrackerFactory
import com.myetherwallet.mewconnect.feature.scan.utils.PermissionHelper
import com.myetherwallet.mewconnect.feature.scan.utils.VibrateUtils
import com.myetherwallet.mewconnect.feature.scan.view.CameraSourcePreview
import com.myetherwallet.mewconnect.feature.scan.viewmodel.ScanViewModel
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.fragment_scan.view.*
import java.io.IOException

/**
 * Created by BArtWell on 11.07.2018.
 */

private const val TAG = "ScanFragment"
private const val START_DELAY = 1000L
private const val CLOSE_DELAY = 2000L
private const val REQUEST_CODE_PERMISSIONS = 101

private const val SUPPORT_EMAIL = "support@myetherwallet.com"
private const val SUPPORT_SUBJECT = "MEWconnect Android v" + BuildConfig.VERSION_NAME + " connection issue"

class ScanFragment : BaseViewModelFragment() {

    companion object {
        fun newInstance() = ScanFragment()
    }

    private lateinit var preview: CameraSourcePreview
    private lateinit var viewModel: ScanViewModel

    private var footerColorBlue: Int = 0
    private var footerColorGrey: Int = 0
    private val toolbarBackgroundBlue = R.drawable.scan_toolbar_background_blue
    private val toolbarBackgroundGrey = R.drawable.scan_toolbar_background_grey

    private val permissionHelper = PermissionHelper(Manifest.permission.CAMERA, ::checkPermissionAndStart)
    private var cameraSource: CameraSource? = null
    private var handler = Handler()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        footerColorBlue = ContextCompat.getColor(requireContext(), R.color.blue)
        footerColorGrey = ContextCompat.getColor(requireContext(), R.color.battleship_grey)

        changeScreenState(null)
        preview = view.scan_preview

        (activity as MainActivity).setStatusBarColor(R.color.status_bar_transparent)

        scan_manual_1.text = StringUtils.fromHtml(scan_manual_1.text.toString())
        scan_manual_2.text = StringUtils.fromHtml(scan_manual_2.text.toString())
        scan_manual_3.text = StringUtils.fromHtml(scan_manual_3.text.toString())

        scan_error_contact.setOnClickListener { LaunchUtils.openMailApp(context, SUPPORT_EMAIL, SUPPORT_SUBJECT) }
        scan_error_try_again.setOnClickListener {
            preview.start(cameraSource)
            changeScreenState(null)
        }

        handler.postDelayed({ checkPermissionAndStart() }, 500)
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = START_DELAY
        animation.fillAfter = true
        scan_animation_view.startAnimation(animation)
    }

    private fun checkPermissionAndStart(isPermissionsGranted: Boolean? = null) {
        if (permissionHelper.checkPermission(requireContext()) || isPermissionsGranted == true) {
            viewModel = viewModel()
            createCameraSource()
            startCameraSource()
            showPermissionRationale(null)
        } else {
            if (isPermissionsGranted == false) {
                if (permissionHelper.shouldShowRequestPermissionsRationale(this)) {
                    showPermissionRationale(true)
                    scan_camera_permission_rationale.visibility = VISIBLE
                } else {
                    showPermissionRationale(false)
                }
            } else {
                permissionHelper.requestPermissions(this)
            }
        }
    }

    private fun showPermissionRationale(shouldOpenDialog: Boolean?) {
        if (shouldOpenDialog == null) {
            scan_camera_permission_rationale.visibility = GONE
        } else {
            if (shouldOpenDialog) {
                setRationaleText(R.string.scan_camera_permission_rationale_dialog, 7, 20) {
                    permissionHelper.requestPermissions(this)
                }
            } else {
                setRationaleText(R.string.scan_camera_permission_rationale_settings, 46, 54) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", requireContext().packageName, null)
                    startActivityForResult(intent, REQUEST_CODE_PERMISSIONS)
                }
            }
            scan_camera_permission_rationale.visibility = VISIBLE
        }
    }

    private fun setRationaleText(@StringRes stringRes: Int, start: Int, end: Int, clickCallback: () -> Unit) {
        val spannableString = SpannableString(getString(stringRes))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                clickCallback()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.WHITE
            }
        }
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        scan_camera_permission_rationale.text = spannableString
        scan_camera_permission_rationale.movementMethod = LinkMovementMethod.getInstance()
        scan_camera_permission_rationale.highlightColor = Color.TRANSPARENT
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        preview.release()
    }

    private fun createCameraSource() {
        val barcodeDetector = BarcodeDetector.Builder(context).build()
        val barcodeFactory = BarcodeTrackerFactory(this@ScanFragment::onBarCodeDetected)
        barcodeDetector.setProcessor(MultiProcessor.Builder<Barcode>(barcodeFactory).build())

        if (!barcodeDetector.isOperational) {
            MewLog.w(TAG, "Detector dependencies are not yet available.")
            val cacheDir = activity?.cacheDir
            if (cacheDir == null || cacheDir.usableSpace * 100 / cacheDir.totalSpace <= 10) {
                MewLog.w(TAG, "Low storage, quit")
                Toast.makeText(context, R.string.scan_low_storage_error, Toast.LENGTH_LONG).show()
                close()
            }
        }

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

        cameraSource = CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build()
    }

    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(activity, code, 0).show()
        }

        if (cameraSource != null) {
            try {
                @Suppress("MissingPermission")
                preview.start(cameraSource!!)
            } catch (e: IOException) {
                MewLog.e(TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
                Toast.makeText(context, R.string.scan_camera_source_error, Toast.LENGTH_LONG).show()
                close()
            }
        }
    }

    private fun onBarCodeDetected(barcode: Barcode) {
        if (barcode.format == Barcode.QR_CODE) {
            MewLog.d(TAG, "QR detected")
            viewModel.connectWithBarcode(barcode.rawValue) { activity?.runOnUiThread { changeScreenState(it) } }
            VibrateUtils.vibrate(context)
            playSound(R.raw.peep_note)
            activity?.runOnUiThread {
                preview.stop()
            }
        }
    }

    private fun changeScreenState(state: ScanViewModel.State?) {
        when (state) {
            ScanViewModel.State.CONNECTING -> switchScreen(scan_connecting_container, toolbarBackgroundGrey, footerColorGrey)
            ScanViewModel.State.CONNECTED -> {
                playSound(R.raw.peep_done)
                switchScreen(scan_done_container, toolbarBackgroundBlue, footerColorBlue)
                handler.postDelayed({ close() }, CLOSE_DELAY)
            }
            ScanViewModel.State.ERROR -> {
                switchScreen(scan_error_container, toolbarBackgroundGrey, footerColorGrey)
                playSound(R.raw.peep_error)
            }
            else -> switchScreen(null, toolbarBackgroundGrey, footerColorGrey)
        }
    }

    private fun switchScreen(selectedView: ViewGroup?, @DrawableRes toolbarDrawable: Int, @ColorInt footerColor: Int) {
        val views = arrayOf(scan_connecting_container, scan_done_container, scan_error_container)
        for (view in views) {
            view.visibility = if (view.id == selectedView?.id) {
                VISIBLE
            } else {
                GONE
            }
        }
        scan_toolbar.setDrawable(toolbarDrawable)
        scan_manual_container.setBackgroundColor(footerColor)
    }

    private fun playSound(@RawRes resId: Int) {
        MediaPlayer.create(context, resId).start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_scan
}
