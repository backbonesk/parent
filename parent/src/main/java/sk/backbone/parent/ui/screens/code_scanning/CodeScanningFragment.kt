package sk.backbone.parent.ui.screens.code_scanning

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import sk.backbone.parent.databinding.FragmentCodeScanningBinding
import sk.backbone.parent.ui.screens.ParentFragment

open class CodeScanningFragment: ParentFragment<FragmentCodeScanningBinding>(FragmentCodeScanningBinding::inflate) {
    var scannedCodeHandler: IScannedCodeHandler? = null
    private var cameraProvider: ProcessCameraProvider? = null
    @Barcode.BarcodeFormat private var formats: Int = Barcode.FORMAT_ALL_FORMATS

    private fun Context.startCamera(@Barcode.BarcodeFormat formats: Int) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            try {
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().apply {
                        setAnalyzer(ContextCompat.getMainExecutor(this@startCamera), CodeAnalyzer(formats) { analyzer, code ->
                            try {
                                scannedCodeHandler?.onCodeScanned(analyzer, code)
                            } catch (throwable: Throwable){
                                scannedCodeHandler?.onErrorDuringScanning(throwable)
                            }
                        })
                    }

                cameraProvider?.unbindAll()

                cameraProvider?.bindToLifecycle(this@CodeScanningFragment, cameraSelector, imageAnalysis, preview)

            } catch (exc: Exception) {

            }
        }, ContextCompat.getMainExecutor(this))
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            context?.startCamera(formats)
        } else {
            activity?.finish()
        }
    }

    fun startScanner(context: Context, @Barcode.BarcodeFormat formats: Int) {
        this@CodeScanningFragment.formats = formats
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            context.startCamera(formats)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}