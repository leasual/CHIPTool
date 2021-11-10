package com.se.wiser.view

//import com.google.android.gms.vision.CameraSource
//import com.google.android.gms.vision.barcode.Barcode
//import com.google.android.gms.vision.barcode.BarcodeDetector
import androidx.appcompat.app.AppCompatActivity

class ScanQRCodeActivity : AppCompatActivity()/*, CHIPBarcodeProcessor.BarcodeDetectionListener*/ {
//    lateinit var binding: ActivityScanQrcodeBinding
//
//    private var cameraSource: CameraSource? = null
//    private var cameraSourceView: CameraSourceView? = null
//    private var barcodeDetector: BarcodeDetector? = null
//    private var cameraStarted = false
//    private var startToNext = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityScanQrcodeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setupViews()
//    }
//
//    private fun setupViews() {
//        cameraSourceView = binding.cameraView
//        binding.include.ivBack.setOnClickListener {
//            this.finish()
//        }
//        if (!hasCameraPermission()) {
//            requestCameraPermission()
//        }
//        initializeBarcodeDetectorAndCamera()
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onResume() {
//        super.onResume()
//        startToNext = false
//        if (hasCameraPermission() && !cameraStarted) {
//            startCamera()
//        }
//    }
//
//    private fun initializeBarcodeDetectorAndCamera() {
//        barcodeDetector?.let { detector ->
//            if (!detector.isOperational) {
//                showCameraUnavailableAlert()
//            }
//            return
//        }
//
//        barcodeDetector = BarcodeDetector.Builder(this).build().apply {
//            setProcessor(CHIPBarcodeProcessor(this@ScanQRCodeActivity))
//        }
//        cameraSource = CameraSource.Builder(this, barcodeDetector)
//            .setFacing(CameraSource.CAMERA_FACING_BACK)
//            .setAutoFocusEnabled(true)
//            .setRequestedFps(30.0f)
//            .build()
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
//            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                showCameraPermissionAlert()
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun handleScannedQrCode(barcode: Barcode) {
//        Handler(Looper.getMainLooper()).post {
//            stopCamera()
//
//            lateinit var payload: SetupPayload
//            try {
//                payload = SetupPayloadParser().parseQrCode(barcode.displayValue)
//                Log.d(TAG, "barCode displayValue=${barcode.displayValue}")
//            } catch (ex: SetupPayloadParser.UnrecognizedQrCodeException) {
//                Log.e(TAG, "Unrecognized QR Code", ex)
//                Toast.makeText(this, "Unrecognized QR Code", Toast.LENGTH_SHORT).show()
//
//                // Restart camera view.
//                if (hasCameraPermission() && !cameraStarted) {
//                    startCamera()
//                }
//                return@post
//            }
//            val deviceInfo = CHIPDeviceInfo(
//                payload.version,
//                payload.vendorId,
//                payload.productId,
//                payload.discriminator,
//                payload.setupPinCode,
//                payload.optionalQRCodeInfo.mapValues { (_, info) ->
//                    QrCodeInfo(info.tag, info.type, info.data, info.int32)
//                }
//            )
//            Log.d(TAG, "setupPinCode= ${payload.setupPinCode} discriminator= ${payload.discriminator}")
//            if (!startToNext) {
//                val intent = Intent(this, PairingActivity::class.java)
//                intent.putExtra("deviceInfo", deviceInfo)
//                intent.putExtra("ipAddress", "192.168.0.135")
//                intent.putExtra("port", 5540)
//                startActivity(intent)
//                startToNext = true
//                this.finish()
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopCamera()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraSourceView?.release()
//    }
//
//    private fun showCameraPermissionAlert() {
//        AlertDialog.Builder(this)
//            .setTitle(R.string.camera_permission_missing_alert_title)
//            .setMessage(R.string.camera_permission_missing_alert_subtitle)
//            .setPositiveButton(R.string.camera_permission_missing_alert_try_again) { _, _ ->
//                requestCameraPermission()
//            }
//            .setCancelable(false)
//            .create()
//            .show()
//    }
//
//    private fun showCameraUnavailableAlert() {
//        AlertDialog.Builder(this)
//            .setTitle(R.string.camera_unavailable_alert_title)
//            .setMessage(R.string.camera_unavailable_alert_subtitle)
//            .setPositiveButton(R.string.camera_unavailable_alert_exit) { _, _ ->
//                this.finish()
//            }
//            .setCancelable(false)
//            .create()
//            .show()
//    }
//
//    @RequiresPermission(Manifest.permission.CAMERA)
//    private fun startCamera() {
//        try {
//            cameraSourceView?.start(cameraSource)
//            cameraStarted = true
//        } catch (e: IOException) {
//            Log.e(TAG, "Unable to start camera source.", e)
//        }
//    }
//
//    private fun stopCamera() {
//        cameraSourceView?.stop()
//        cameraStarted = false
//    }
//
//    private fun hasCameraPermission(): Boolean {
//        return (PackageManager.PERMISSION_GRANTED
//                == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
//    }
//
//    private fun requestCameraPermission() {
//        val permissions = arrayOf(Manifest.permission.CAMERA)
//        requestPermissions(permissions, REQUEST_CODE_CAMERA_PERMISSION)
//    }
//
//    companion object {
//        private const val TAG = "ScanQRCodeActivity"
//        private const val REQUEST_CODE_CAMERA_PERMISSION = 100;
//    }
}