package sk.backbone.parent.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue


const val iso8601Format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
const val iso8601FormatUtcStrict = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val iso84FormatUtcStrictNoMillis = "yyyy-MM-dd'T'HH:mm:ssZ"
const val iso8601DateOnlyFormat = "yyyy-MM-dd"
const val iso8601FormatWithoutSeconds = "yyyy-MM-dd'T'HH:mmZZZZZ"

fun getDateWithFirstMonthDay(timezone: TimeZone = TimeZone.getDefault()): Date = Calendar.getInstance(timezone).apply {
    this.set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
    this.set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
    this.set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
    this.set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
    this.set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
}.time

fun getDateWithLastMonthDay(timezone: TimeZone = TimeZone.getDefault()): Date = Calendar.getInstance(timezone).apply {
    this.set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    this.set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
    this.set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
    this.set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
    this.set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
}.time

fun getStartOfCurrentDay(timezone: TimeZone = TimeZone.getDefault()): Date = Calendar.getInstance(timezone).apply {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}.time

fun Date.getStartOfDay(timezone: TimeZone = TimeZone.getDefault()): Date {
    val current = Calendar.getInstance().apply {
        time = this@getStartOfDay
    }

    return Calendar.getInstance(timezone).also {
        it.set(Calendar.YEAR, current.get(Calendar.YEAR))
        it.set(Calendar.MONTH, current.get(Calendar.MONTH))
        it.set(Calendar.DAY_OF_YEAR, current.get(Calendar.DAY_OF_YEAR))
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }.time
}

fun Date.getEndOfDay(timezone: TimeZone = TimeZone.getDefault()): Date {
    return getStartOfDay(timezone).getEndOfDayFromStartOfDay(timezone)
}

fun Date.getEndOfDayFromStartOfDay(timezone: TimeZone = TimeZone.getDefault()): Date {
    return Calendar.getInstance(timezone).also { calendar ->
        calendar.time = Date().apply { time = this@getEndOfDayFromStartOfDay.getTomorrow().time - 1 }
    }.time
}

fun Date.getTomorrow(): Date {
    return Calendar.getInstance().apply {
        time = Date().apply { time = this@getTomorrow.time + (3600 * 24 * 1000) }
    }.time
}

fun Date.isDifferentMonth(date: Date): Boolean {
    val cal1 = Calendar.getInstance().also { it.time = date }
    val cal2 = Calendar.getInstance().also { it.time = this }

    return cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(
        Calendar.YEAR
    )
}

fun isDifferentDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().also { it.time = date1 }
    val cal2 = Calendar.getInstance().also { it.time = date2 }

    return cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR) || cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)
}

fun Date.getDaysBetween(dest: Date): Int {
    val diff = abs(this.time - dest.time)
    val dayCount = diff.toDouble() / (24 * 60 * 60 * 1000)
    return dayCount.toInt()
}

fun Date.isBetween(startDate: Date, endDate: Date): Boolean {
    return this.time >= startDate.time && this.time <= endDate.time
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    return !isDifferentDay(date1, date2)
}

fun toSlovakDate(value: String): Date? {
    return SimpleDateFormat(iso8601Format, Locale("sk")).apply {
        timeZone = TimeZone.getTimeZone("Europe/Bratislava")
    }.parse(value)
}

fun fromUtcString(value: String?): Date? {
    return value?.let {
        SimpleDateFormat(iso8601Format, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(it)
    }
}

fun fromUtcStringDateOnly(value: String?): Date? {
    return value?.let {
        SimpleDateFormat(iso8601DateOnlyFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(it)
    }
}

fun toUtcString(value: Date?): String? {
    val dateFormat = SimpleDateFormat(iso8601Format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    return value?.let { dateFormat.format(it) }
}

fun toUtcStringWithoutSeconds(value: Date?): String? {
    val dateFormat = SimpleDateFormat(iso8601FormatWithoutSeconds, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    return value?.let { dateFormat.format(it) }
}

fun formatIsoDateOnlyToLocalDate(inputDate: String?): String? {
    return inputDate?.let {
        val date = SimpleDateFormat(iso8601DateOnlyFormat, Locale.getDefault()).parse(inputDate)

        return extractLocallyFormattedDateOnly(date)
    }
}

fun Date.getIso8601DateOnlyString(): String {
    return SimpleDateFormat(iso8601DateOnlyFormat, Locale.getDefault()).format(this)
}

fun Date.iso84FormatUtcStrictNoMillis(): String {
    return SimpleDateFormat(iso84FormatUtcStrictNoMillis, Locale.getDefault()).format(this)
}

fun fromIsoDateFormatUtcNoMillis(value: String?): Date? {
    return value?.let {
        SimpleDateFormat(iso84FormatUtcStrictNoMillis, Locale.getDefault()).parse(it)
    }
}

fun formatDefaultLocaleDateToIsoDateOnly(input: String?): String? {
    val date = input?.let { localShortDateFormatter.parse(input) }
    return date?.let { SimpleDateFormat(iso8601DateOnlyFormat, Locale.getDefault()).format(date) }
}

fun extractLocallyFormattedDateOnly(date: Date?): String? {
    return date?.let { return localShortDateFormatter.format(date) }
}

fun parseLocallyFormattedDateFromDateOnlyString(date: String?): Date? {
    return date?.let { return localShortDateFormatter.parse(date) }
}

fun getDifferenceIn24HFormat(first: Date, second: Date): String {
    val millisBetweenTotal = (first.time - second.time).absoluteValue
    val secondsBetweenTotal = millisBetweenTotal / 1000
    val millis = (secondsBetweenTotal % 1000).toString().padStart(2, '0')
    val seconds = (secondsBetweenTotal % 60).toString().padStart(2, '0')
    val minutes = ((secondsBetweenTotal / 60).toInt() % 60).toString().padStart(2, '0')
    val hours = ((secondsBetweenTotal / 3600).toInt()).toString().padStart(2, '0')

    return "${hours}:${minutes}:${seconds}:${millis}"
}

private val localShortDateFormatter: SimpleDateFormat
    get() {
        val simpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()) as SimpleDateFormat
        val pattern = simpleDateFormat.toLocalizedPattern()
        return SimpleDateFormat(pattern, Locale.getDefault())
    }