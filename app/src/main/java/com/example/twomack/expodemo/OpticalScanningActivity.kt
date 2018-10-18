package com.example.twomack.expodemo

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.util.*

class OpticalScanningActivity : AppCompatActivity() {

    private var cameraPermissionReqCode = 250
    var tvCardText: TextView? = null
    var lastText: String? = null
    private var barcodeView: DecoratedBarcodeView? = null
    private var beepManager: BeepManager? = null


    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {

            updateText(result.text)

            if (result.text == null || result.text == lastText) {
                //Prevent duplicate scans
                return
            }

            lastText = result.text
            beepManager?.setVibrateEnabled(true)
            beepManager?.playBeepSoundAndVibrate()
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openCameraWithPermission()
        setContentView(R.layout.activity_test_scan)
        tvCardText = findViewById(R.id.tv_code_text) as TextView
        startContinuousScanner()
    }

    override fun onPause() {
        super.onPause()
        barcodeView?.pause()
    }

    override fun onPostResume() {
        super.onPostResume()
        barcodeView?.resume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.hardware_menu_item -> {
                //startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            R.id.optical_menu_item -> {
                //startActivity(Intent(this, OpticalScanningActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        //respond to menu item selection

    }


    private fun startContinuousScanner() {
        barcodeView = findViewById<View>(R.id.barcode_view) as DecoratedBarcodeView?
        val formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.UPC_A, BarcodeFormat.UPC_A,
                BarcodeFormat.PDF_417, BarcodeFormat.AZTEC, BarcodeFormat.CODABAR, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128, BarcodeFormat.DATA_MATRIX, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.ITF,
                BarcodeFormat.MAXICODE, BarcodeFormat.QR_CODE, BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED, BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_EAN_EXTENSION)
        barcodeView?.getBarcodeView()?.setDecoderFactory(DefaultDecoderFactory(formats))
        barcodeView?.decodeContinuous(callback)
        barcodeView?.setStatusText("Center a barcode in the rectangle to scan it")
        beepManager = BeepManager(this)
    }

    private fun updateText(scanCode: String) {
        tvCardText?.setText(scanCode)
    }

    override fun onResume() {

        // TODO Auto-generated method stub
        super.onResume()
        barcodeView?.resume()

    }

    private var askedPermission = false

    @TargetApi(23)
    private fun openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeView?.resume()
        } else if (!askedPermission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraPermissionReqCode)
            askedPermission = true
        } else {
            // Wait for permission result
        }
    }


    /**
     * Call from Activity#onRequestPermissionsResult
     * @param requestCode The request code passed in [ActivityCompat.requestPermissions].
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     */


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == cameraPermissionReqCode) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                barcodeView?.resume()
            } else {
                // TODO: display better error message.
                //displayFrameworkBugMessageAndExit()
            }
        }
    }

}
