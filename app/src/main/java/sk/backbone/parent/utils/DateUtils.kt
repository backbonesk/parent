package sk.backbone.parent.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val iso8061Format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
const val iso8061DateOnlyFormat = "yyyy-MM-dd"
const val iso8061FormatWithoutSeconds = "yyyy-MM-dd'T'HH:mmXXX"

fun getDateWithFirstMonthDay() = Calendar.getInstance().apply {
    this.set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
    this.set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
    this.set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
    this.set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
    this.set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
}.time

fun getDateWithLastMonthDay() = Calendar.getInstance().apply {
    this.set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    this.set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
    this.set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
    this.set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
    this.set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
}.time

fun getStartOfCurrentDay(): Date = Calendar.getInstance().apply {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}.time

fun Date.getStartOfDay(): Date {
    return Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }.time
}

fun Date.getEndOfDay(): Date {
    return Calendar.getInstance().also {
        it.time.time = getStartOfDay().getTomorrow().time - 1
    }.time
}

fun Date.getTomorrow(): Date {
    return Calendar.getInstance().also {
        it.time = this
        it.set(Calendar.DAY_OF_YEAR, it.get(Calendar.DAY_OF_YEAR) + 1)
    }.time
}

fun Date.isDifferentMonth(date: Date): Boolean {
    val cal1 = Calendar.getInstance().also { it.time = date }
    val cal2 = Calendar.getInstance().also { it.time = this }

    return cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(
        Calendar.YEAR)
}

fun isDifferentDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().also { it.time = date1 }
    val cal2 = Calendar.getInstance().also { it.time = date2 }

    return cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR) || cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)
}

fun Date.getDaysBetween(dest: Date): Int {
    val diff = abs(this.time - dest.time)
    val dayCount = diff.toFloat() / (24 * 60 * 60 * 1000)
    return dayCount.toInt()
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    return !isDifferentDay(date1, date2)
}

fun toSlovakDate(value: String): Date? {
    return SimpleDateFormat(iso8061Format, Locale("sk")).apply {
        timeZone = TimeZone.getTimeZone("Europe/Bratislava")
    }.parse(value)
}

fun fromUtcString(value: String?): Date? {
    return value?.let { SimpleDateFormat(iso8061Format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.parse(it) }
}

fun fromUtcStringDateOnly(value: String?): Date? {
    return value?.let { SimpleDateFormat(iso8061DateOnlyFormat, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.parse(it) }
}

fun toUtcString(value: Date?): String? {
    val dateFormat = SimpleDateFormat(iso8061Format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    return value?.let { dateFormat.format(it) }
}

fun toUtcStringWithoutSeconds(value: Date?): String? {
    val dateFormat = SimpleDateFormat(iso8061FormatWithoutSeconds, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    return value?.let { dateFormat.format(it) }
}

fun formatIsoDateOnlyToLocalDate(inputDate: String?): String? {
    return inputDate?.let {
        val date = SimpleDateFormat(iso8061DateOnlyFormat, Locale.getDefault()).parse(inputDate)

        return extractLocallyFormattedDateOnly(date)
    }
}

fun Date.getIso8061DateOnlyString(): String {
    return SimpleDateFormat(iso8061DateOnlyFormat, Locale.getDefault()).format(this)
}

fun formatDefaultLocaleDateToIsoDateOnly(input: String?): String? {
    val date = input?.let { localShortDateFormatter.parse(input) }
    return date?.let { SimpleDateFormat(iso8061DateOnlyFormat, Locale.getDefault()).format(date) }
}

fun extractLocallyFormattedDateOnly(date: Date?): String? {
    return date?.let { return localShortDateFormatter.format(date) }
}

fun parseLocallyFormattedDateFromDateOnlyString(date: String?): Date? {
    return date?.let { return localShortDateFormatter.parse(date) }
}

private val localShortDateFormatter: SimpleDateFormat
    get() {
        val simpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()) as SimpleDateFormat
        val pattern = simpleDateFormat.toLocalizedPattern()
        return SimpleDateFormat(pattern, Locale.getDefault())
    }