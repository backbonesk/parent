package sk.backbone.parent.ui.screens.code_scanning

import android.os.Bundle
import com.google.mlkit.vision.barcode.common.Barcode
import sk.backbone.parent.databinding.ActivityCodeScanningBinding
import sk.backbone.parent.ui.screens.ParentActivity
import sk.backbone.parent.ui.screens.ParentViewBindingActivity

abstract class CodeScanningActivity: ParentViewBindingActivity<ActivityCodeScanningBinding>(ActivityCodeScanningBinding::inflate), IScannedCodeHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.scanningFragment.getFragment<CodeScanningFragment>().apply {
            this.scannedCodeHandler = this@CodeScanningActivity
            this.startScanner(this@CodeScanningActivity, Barcode.FORMAT_QR_CODE)
        }
    }

    abstract override fun onCodeScanned(analyzer: CodeAnalyzer, barcode: Barcode)
}