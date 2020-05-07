package com.myetherwallet.mewconnect.feature.scan.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.platform.NetworkHandler
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.core.utils.LaunchUtils
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.StringUtils
import com.myetherwallet.mewconnect.feature.main.activity.MainActivity
import com.myetherwallet.mewconnect.feature.main.receiver.NetworkStateReceiver
import com.myetherwallet.mewconnect.feature.scan.utils.PermissionHelper
import com.myetherwallet.mewconnect.feature.scan.utils.VibrateUtils
import com.myetherwallet.mewconnect.feature.scan.view.BarcodeScanningProcessor
import com.myetherwallet.mewconnect.feature.scan.view.CameraSource
import com.myetherwallet.mewconnect.feature.scan.view.CameraSourcePreview
import com.myetherwallet.mewconnect.feature.scan.viewmodel.ScanViewModel
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.fragment_scan.view.*
import java.io.IOException
import javax.inject.Inject

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

    @Inject
    lateinit var networkHandler: NetworkHandler
    private lateinit var preview: CameraSourcePreview
    private lateinit var viewModel: ScanViewModel

    private var footerColorBlue: Int = 0
    private var footerColorGrey: Int = 0
    private val toolbarBackgroundBlue = R.drawable.scan_toolbar_background_blue
    private val toolbarBackgroundGrey = R.drawable.scan_toolbar_background_grey
    private var isStarted = false
    private var isScanStopped = true

    private val permissionHelper = PermissionHelper(Manifest.permission.CAMERA, ::checkPermissionAndStart)
    private val networkStateReceiver = NetworkStateReceiver(::setConnectedStatus)
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
            startCamera()
            changeScreenState(null)
        }
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
        addOnResumeListener {
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
        if (isStarted) {
            startCameraSource()
        } else {
            handler.postDelayed({
                if (!isStateSaved) {
                    isStarted = true
                    checkPermissionAndStart()
                }
            }, 500)
            val animation = AlphaAnimation(1f, 0f)
            animation.duration = START_DELAY
            animation.fillAfter = true
            scan_animation_view.startAnimation(animation)
        }
        setConnectedStatus()
        networkStateReceiver.register(requireContext())
    }

    override fun onPause() {
        networkStateReceiver.unregister(requireContext())
        stopCamera()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        preview.release()
    }


    private fun createCameraSource() {
        if (cameraSource == null) {
            cameraSource = CameraSource(requireActivity())
        }

        try {
            cameraSource?.setMachineLearningFrameProcessor(BarcodeScanningProcessor(::onBarCodeDetected))
        } catch (e: FirebaseMLException) {
            MewLog.e(TAG, "Can't create camera source")
        }
    }

    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(activity, code, 0).show()
        }
        cameraSource?.let {
            try {
                startCamera()
                isScanStopped = false
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }
        }
    }

    private fun onBarCodeDetected(barcode: FirebaseVisionBarcode) {
        if (isScanStopped) {
            return
        }
        if (barcode.format == Barcode.QR_CODE) {
            MewLog.d(TAG, "QR detected")
            VibrateUtils.vibrate(context)
            playSound(R.raw.peep_note)
            activity?.runOnUiThread {
                stopCamera()
            }
            val barcodeValue = barcode.rawValue
            if (barcodeValue == null) {
                activity?.runOnUiThread {
                    Toast.makeText(context, R.string.scan_camera_unreadable_qr, Toast.LENGTH_LONG).show()
                    startCamera()
                }
            } else {
                if ("^[^_]+_[0-9a-f]+_[0-9a-f]+$".toRegex() matches barcodeValue) {
                    viewModel.connectWithBarcode(barcodeValue) {
                        activity?.runOnUiThread {
                            if (!isStateSaved) {
                                changeScreenState(it)
                            }
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, R.string.scan_camera_wrong_qr, Toast.LENGTH_LONG).show()
                        startCamera()
                    }
                }
            }
        }
    }

    private fun startCamera() {
        isScanStopped = false
        preview.start(cameraSource)
    }

    private fun stopCamera() {
        isScanStopped = true
        preview.stop()
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

    private fun setConnectedStatus() {
        if (networkHandler.isConnected == true) {
            MewLog.d(TAG, "Connected")
            startCameraSource()
            scan_offline_container.visibility = GONE
        } else {
            MewLog.d(TAG, "Disconnected")
            scan_offline_container.visibility = VISIBLE
            stopCamera()
        }
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
