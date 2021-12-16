package sk.backbone.parent.ui.screens.code_scanning

import com.google.mlkit.vision.barcode.common.Barcode

interface IScannedCodeHandler {
    fun onCodeScanned(analyzer: CodeAnalyzer, barcode: Barcode)
}