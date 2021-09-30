package sk.backbone.parent.ui.screens.code_scanning

import com.google.mlkit.vision.barcode.Barcode
import sk.backbone.parent.ui.screens.code_scanning.CodeAnalyzer

interface IScannedCodeHandler {
    fun onCodeScanned(analyzer: CodeAnalyzer, barcode: Barcode)
}