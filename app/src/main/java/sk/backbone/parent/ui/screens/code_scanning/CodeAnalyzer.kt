package sk.backbone.parent.ui.screens.code_scanning

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.Closeable

class CodeAnalyzer(@Barcode.BarcodeFormat private val formats: Int, private val onCodeScanned: (CodeAnalyzer, Barcode) -> Unit) : ImageAnalysis.Analyzer, Closeable {
    private val scanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(formats)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { codes ->
                    codes.firstOrNull()?.let {
                        onCodeScanned(this, it)
                    }
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    override fun close() {
        scanner.close()
    }
}
