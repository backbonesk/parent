package sk.backbone.parent.ui.screens.code_scanning

import android.os.Bundle
import com.google.mlkit.vision.barcode.Barcode
import sk.backbone.parent.databinding.ActivityCodeScanningBinding
import sk.backbone.parent.ui.screens.ParentActivity

abstract class CodeScanningActivity: ParentActivity<ActivityCodeScanningBinding>(ActivityCodeScanningBinding::inflate), IScannedCodeHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.scanningFragment.getFragment<CodeScanningFragment>().apply {
            this.scannedCodeHandler = this@CodeScanningActivity
            this.startScanner(this@CodeScanningActivity, Barcode.FORMAT_QR_CODE)
        }
    }

    abstract override fun onCodeScanned(analyzer: CodeAnalyzer, barcode: Barcode)
}