package sk.backbone.parent.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.load.ImageHeaderParser.UNKNOWN_ORIENTATION
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import sk.backbone.parent.databinding.ActivityCameraBinding
import sk.backbone.parent.utils.afterMeasured
import sk.backbone.parent.utils.setSafeOnClickListener

// Future Todo: Front/Back camera switching

@AndroidEntryPoint
class CameraActivity : ParentActivity<ActivityCameraBinding>(ActivityCameraBinding::inflate) {
    private val supportedOrientationModes = listOf(
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
    )

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var imageCapture : ImageCapture? = null
    private var preview : Preview? = null
    private var cameraSelector : CameraSelector? = null
    private var camera: Camera? = null

    private var currentRotation: Int = Surface.ROTATION_0

    private val imageUri: Uri? by lazy {
        intent.getParcelableExtra(IMAGE_URI_EXTRAS) as Uri?
    }

    private val lensFacing: Int by lazy {
        intent.getIntExtra(LENS_FACING_EXTRAS, CameraSelector.LENS_FACING_BACK)
    }

    private val desiredOrientation: Int by lazy {
        intent.getIntExtra(ORIENTATION_EXTRAS, ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startCamera()
        } else {
            finish()
        }
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this, ) {
            override fun onOrientationChanged(orientation: Int) {
                updateOrientations(orientation)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!supportedOrientationModes.contains(desiredOrientation)){
            throw IllegalArgumentException("Orientation not supported.")
        }

        if(imageUri == null){
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        (intent.getSerializableExtra(LAYOUT_OVERLAY_EXTRAS) as Int?)?.let {
            View.inflate(this, it, viewBinding.overlayHolder)
        }

        setRotationAnimation()

        startCamera()
    }

    override fun onResume() {
        super.onResume()

        if(desiredOrientation == ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR || desiredOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE){
            orientationEventListener.enable()
        } else {
            updateOrientations(null)
        }
    }

    override fun onPause() {
        super.onPause()

        orientationEventListener.disable()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCamera(cameraProvider : ProcessCameraProvider) {
        preview = Preview.Builder()
            .setTargetRotation(currentRotation)
            .build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        preview?.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(currentRotation)
            .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()


        camera = cameraProvider.bindToLifecycle(this, cameraSelector!!, imageCapture, preview)

        viewBinding.shutter.setSafeOnClickListener {

            contentResolver.openOutputStream(imageUri!!)?.let { outputStream ->
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()

                imageCapture?.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(error: ImageCaptureException) {

                        }
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                )
            }
        }

        viewBinding.cameraPreview.afterMeasured {
            viewBinding.cameraPreview.setOnTouchListener { _, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            viewBinding.cameraPreview.width.toFloat(), viewBinding.cameraPreview.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        try {
                            camera?.cameraControl?.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()
                            )
                        } catch (e: CameraInfoUnavailableException) {
                            Log.d("ERROR", "cannot access camera", e)
                        }
                        true
                    }
                    else -> false // Unhandled event.
                }
            }
        }
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if(camera == null){
                cameraProviderFuture = ProcessCameraProvider.getInstance(this)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    setupCamera(cameraProvider)
                }, ContextCompat.getMainExecutor(this))
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun updateOrientations(orientation: Int?) {
        if (orientation == UNKNOWN_ORIENTATION) {
            return
        }

        val activityOrientation: Int

        when (desiredOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> {
                currentRotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270.also { activityOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE }
                    in 135 until 225 -> Surface.ROTATION_180.also { activityOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT }
                    in 225 until 315 -> Surface.ROTATION_90.also { activityOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
                    else -> Surface.ROTATION_0.also { activityOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
                }
            }
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> {
                currentRotation = when (orientation) {
                    in 20 until 160 -> Surface.ROTATION_270.also { activityOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE }
                    in 200 until 340 -> Surface.ROTATION_90.also { activityOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
                    else -> currentRotation.also { activityOrientation = requestedOrientation }
                }
            }
            else -> {
                activityOrientation = desiredOrientation
                currentRotation = when(desiredOrientation) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> {
                        Surface.ROTATION_0
                    }
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> {
                        Surface.ROTATION_90
                    }
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> {
                        Surface.ROTATION_180
                    }
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> {
                        Surface.ROTATION_270
                    }
                    else -> Surface.ROTATION_0
                }
            }
        }

        if(requestedOrientation != activityOrientation){
            requestedOrientation = activityOrientation
            fixRotations()
        }
    }

    private fun setRotationAnimation() {
        val rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE
        val win: Window = window
        val winParams: WindowManager.LayoutParams = win.attributes
        winParams.rotationAnimation = rotationAnimation
        win.attributes = winParams
    }

    private fun fixRotations(){
        imageCapture?.targetRotation = currentRotation
        preview?.targetRotation = currentRotation

        viewBinding.shutter.rotation = currentRotation * 90f

        viewBinding.shutter.updateLayoutParams<LayoutParams> {
            when(currentRotation){
                Surface.ROTATION_0 -> {
                    width = 0
                    height = WRAP_CONTENT
                    verticalBias = 0.95f
                    horizontalBias = 0.5f
                }
                Surface.ROTATION_180 -> {
                    width = 0
                    height = WRAP_CONTENT
                    verticalBias = 0.05f
                    horizontalBias = 0.5f
                }
                Surface.ROTATION_90 -> {
                    width = WRAP_CONTENT
                    height = 0
                    verticalBias = 0.5f
                    horizontalBias = 0.95f
                }
                Surface.ROTATION_270 -> {
                    width = WRAP_CONTENT
                    height = 0
                    verticalBias = 0.5f
                    horizontalBias = 0.05f
                }
            }
        }
    }

    companion object {
        private const val IMAGE_URI_EXTRAS = "IMAGE_URI_EXTRAS"
        private const val ORIENTATION_EXTRAS = "ORIENTATION_EXTRAS"
        private const val LAYOUT_OVERLAY_EXTRAS = "LAYOUT_OVERLAY_EXTRAS"
        private const val LENS_FACING_EXTRAS = "LENS_FACING_EXTRAS"

        fun createIntent(context: Context, imageUri: Uri, orientation: Int  = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, @CameraSelector.LensFacing lensFacing: Int = CameraSelector.LENS_FACING_BACK, layoutOverlay: Int? = null): Intent {
            return Intent(context, CameraActivity::class.java).apply {
                putExtra(IMAGE_URI_EXTRAS, imageUri)
                putExtra(ORIENTATION_EXTRAS, orientation)
                putExtra(LAYOUT_OVERLAY_EXTRAS, layoutOverlay)
                putExtra(LENS_FACING_EXTRAS, lensFacing)
            }
        }

        fun startActivity(context: Context, imageUri: Uri, @CameraSelector.LensFacing lensFacing: Int = CameraSelector.LENS_FACING_BACK, orientation: Int = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, layoutOverlay: Int? = null) {
            context.startActivity(createIntent(context, imageUri, orientation, lensFacing, layoutOverlay))
        }
    }
}