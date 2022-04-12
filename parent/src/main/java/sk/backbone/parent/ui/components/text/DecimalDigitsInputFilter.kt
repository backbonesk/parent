package sk.backbone.parent.ui.components.text

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.text.InputFilter
import android.text.Spanned
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsBeforeSeparator: Int = 10, digitsAfterSeparator: Int = 10) : InputFilter {
    private val mPattern: Pattern

    init {
        val before = "(-?\\d{1,$digitsBeforeSeparator})"
        val after = "(\\d{1,$digitsAfterSeparator})"
        val separator = "[\\${(DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat).decimalFormatSymbols.decimalSeparator}]"
        val numberRegex = StringBuilder()
            .append("(-)")
            .append("|")
            .append("($before$separator$after)")
            .append("|")
            .append("($before$separator)")
            .append("|")
            .append("($before)")
            .toString()
        mPattern = Pattern.compile(numberRegex)
    }

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val input = dest.toString().substring(0, dstart) + source.subSequence(start, end) + dest.toString().substring(dend)
        val matcher: Matcher = mPattern.matcher(input)
        return if (!matcher.matches()) {
            ""
        } else null
    }

    companion object {
        val decimalSeparator get() = (DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat).decimalFormatSymbols.decimalSeparator.toString()

        fun getDecimalSymbolsForLocale(signed: Boolean = true, decimal: Boolean = true): String {
            val decFormat: DecimalFormat = DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat
            val decimalSymbols: DecimalFormatSymbols = decFormat.decimalFormatSymbols
            val digits = String(decimalSymbols.digits)
            val decimalSeparator = decimalSeparator
            val minus = decimalSymbols.minusSign.toString()

            var symbols = digits

            if(signed){
                symbols += minus
            }

            if(decimal){
                symbols += decimalSeparator
            }

            return symbols
        }
    }
}