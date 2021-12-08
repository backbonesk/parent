package sk.backbone.parent.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import sk.backbone.parent.databinding.ActivityCameraBinding
import sk.backbone.parent.utils.setSafeOnClickListener

@AndroidEntryPoint
class CameraActivity : ParentActivity<ActivityCameraBinding>(ActivityCameraBinding::inflate) {
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var imageCapture : ImageCapture? = null
    private var preview : Preview? = null

    private val rotation: Int get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.rotation
        } else {
            windowManager.defaultDisplay.rotation
        } ?: 0
    }

    private val imageUri: Uri? by lazy {
        intent.getParcelableExtra(IMAGE_URI_EXTRAS) as Uri?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(imageUri == null){
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        (intent.getSerializableExtra(LAYOUT_OVERLAY_EXTRAS) as Int?)?.let {
            View.inflate(this, it, viewBinding.overlayHolder)
        }

        (intent.getSerializableExtra(ORIENTATION_EXTRAS) as Int?)?.let {
            requestedOrientation = it
        }

        viewBinding.cameraPreview

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            setupCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        setRotationAnimation()
    }

    private fun setupCamera(cameraProvider : ProcessCameraProvider) {
        preview = Preview.Builder()
            .setTargetRotation(rotation)
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview?.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(rotation)
            .build()


        var camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)

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
    }

    override fun onResume() {
        super.onResume()

        fixRotations()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        fixRotations()
    }

    private fun setRotationAnimation() {
        val rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE
        val win: Window = window
        val winParams: WindowManager.LayoutParams = win.attributes
        winParams.rotationAnimation = rotationAnimation
        win.attributes = winParams
    }

    private fun fixRotations(){
        imageCapture?.targetRotation = rotation
        preview?.targetRotation = rotation

        viewBinding.shutter.rotation = rotation * 90f

        viewBinding.shutter.updateLayoutParams<LayoutParams> {

            when(rotation){
                Surface.ROTATION_0 -> {
                    width = 0
                    height = WRAP_CONTENT
                    verticalBias = 0.95f
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

        fun createIntent(context: Context, imageUri: Uri, orientation: Int? = null, layoutOverlay: Int? = null): Intent {
            return Intent(context, CameraActivity::class.java).apply {
                putExtra(IMAGE_URI_EXTRAS, imageUri)
                putExtra(ORIENTATION_EXTRAS, orientation)
                putExtra(LAYOUT_OVERLAY_EXTRAS, layoutOverlay)
            }
        }

        fun startActivity(context: Context, imageUri: Uri, orientation: Int? = null, layoutOverlay: Int? = null) {
            context.startActivity(createIntent(context, imageUri, orientation, layoutOverlay))
        }
    }
}