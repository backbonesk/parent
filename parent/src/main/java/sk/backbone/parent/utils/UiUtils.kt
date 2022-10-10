package sk.backbone.parent.utils

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import sk.backbone.parent.ui.components.SafeClickListener
import sk.backbone.parent.ui.screens.ParentFragment
import sk.backbone.parent.ui.validations.IValidableInput
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*


fun View.setSafeOnClickListener(action: (View) -> Unit) {
    val safeClickListener =
        SafeClickListener {
            action(it)
        }
    setOnClickListener(safeClickListener)
}

fun View.hideKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun createRecyclerViewHolder(parent: ViewGroup, layoutId: Int): View {
    return LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
}

fun View.scrollToView() {
    var parent: ViewParent?
    parent = this.parent

    while (parent !is ScrollView && parent != null) {
        parent = parent.parent
    }

    if (parent is ScrollView) {
        parent.post {
            parent.smoothScrollTo(0, this.top)
        }
    } else {
        val rect = Rect(0, 0, this.width, this.height)
        this.requestRectangleOnScreen(rect, false)
    }
}

fun Context.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}

fun Context.screenHeight(): Int {
    return resources.displayMetrics.heightPixels
}

fun scrollToFirstView(views: MutableList<View>) {
    views.sortBy { input -> input.top }
    val firstView = views.firstOrNull()
    firstView?.scrollToView()
}

fun Int.convertDensityPointsToPixels(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}

fun Context.sendActionToOtherApp(message: String, title: String, subject: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    shareIntent.putExtra(Intent.EXTRA_TEXT, message)

    ContextCompat.startActivity(this, Intent.createChooser(shareIntent, title), null)
}

fun TextView?.setTextAndUpdateVisibility(input: CharSequence?) {
    if (input.isNullOrEmpty()) {
        this?.visibility = GONE
    } else {
        this?.visibility = VISIBLE
    }

    this?.text = input
}

fun ImageView.loadWithGlide(uri: Uri?, options: RequestOptions? = null) {
    Glide.with(this).load(uri).apply {
        options?.let { apply(it) }
    }.into(this)
}


fun ImageView.loadWithGlide(bitmap: Bitmap?, options: RequestOptions? = null) {
    Glide.with(this).load(bitmap).apply {
        options?.let { apply(it) }
    }.into(this)
}

fun ImageView.loadResource(url: String?, defaultImage: Int? = null, options: RequestOptions = RequestOptions().apply { centerCrop() }) {
    loadResource(url?.let { GlideUrl(it) }, defaultImage, options)
}

fun ImageView.loadResource(url: GlideUrl?, defaultImage: Int? = null, options: RequestOptions = RequestOptions().apply { centerCrop() }) {
    Glide.with(this).clear(this)
    Glide.with(this).load(url).apply(options).apply {
        if (defaultImage != null) {
            placeholder(defaultImage).fallback(defaultImage).into(this@loadResource)
        } else {
            into(this@loadResource)
        }
    }
}

fun Int.getResourceStringValue(context: Context): String {
    return context.resources.getString(this)
}

fun BigDecimal.getAsEuros(): String {
    val euroSign = "€"
    return String.format("$this $euroSign")
}

fun String.toAnyCultureBigDecimal(): BigDecimal? {
    return replace(",", ".").toBigDecimalOrNull()
}

fun <T> ViewGroup.getViewsByType(tClass: Class<T>): ArrayList<T> {
    val result: ArrayList<T> = ArrayList()

    val childCount = childCount

    for (index in 0 until childCount) {
        val child = getChildAt(index)

        if (child is ViewGroup) {
            result.addAll(child.getViewsByType(tClass))
        }

        if (tClass.isInstance(child)) {
            val instance = tClass.cast(child)
            instance?.let { result.add(it) }
        }
    }
    return result
}

fun ViewGroup.validateInputs(): Boolean {
    val inputs = getViewsByType(IValidableInput::class.java).toList()
    return validateInputs(inputs)
}

fun validateInputs(inputs: List<IValidableInput<*>>): Boolean {
    val invalidInputs = mutableListOf<View>()

    var areInputsValid = true
    for (input in inputs) {

        val isInputValid = input.validate()

        areInputsValid = isInputValid && areInputsValid

        if (input is View && !isInputValid) {
            invalidInputs.add(input)
        }
    }

    if (!areInputsValid) {
        scrollToFirstView(invalidInputs)
    }

    return areInputsValid
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.openInBrowser(link: String, onErrorAction: (() -> Unit)?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    val packageManager = packageManager
    if (browserIntent.resolveActivity(packageManager) != null) {
        startActivity(browserIntent)
    } else {
        onErrorAction?.invoke()
    }
}

inline fun <reified T> AppCompatActivity.returnNewOrReturnExisting(defaultFragment: T): T {
    var result: T = defaultFragment
    for (existingFragment in supportFragmentManager.fragments) {
        if (existingFragment is T) {
            result = existingFragment
            break
        }
    }

    return result
}

fun BigDecimal.formatAsCurrency(currencyCode: String, minimumFractionDigits: Int = 2, maximumFractionDigits: Int = 2): String {
    return NumberFormat.getCurrencyInstance().apply {
        this.minimumFractionDigits = minimumFractionDigits
        this.maximumFractionDigits = maximumFractionDigits
        this.currency = Currency.getInstance(currencyCode)
    }.format(this)
}

fun <TFragment> FragmentManager.showFragment(@IdRes fragmentHolder: Int, fragment: TFragment, onFragmentShown: (() -> (Unit))? = null) where TFragment : ParentFragment<*> {
    val fragmentTransaction = this.beginTransaction()
    var shouldShow = false

    val hiddenFragments = mutableListOf<ParentFragment<*>>()




    for (addedFragment in this.fragments) {
        if ((addedFragment.tag != null && fragment.tag != null && addedFragment.tag == fragment.tag) ||
            addedFragment is ParentFragment<*> && addedFragment.identifier == fragment.identifier
        ) {
            if (addedFragment == fragment) {
                shouldShow = true
            } else {
                fragmentTransaction.remove(addedFragment)
            }
        } else {
            if ((addedFragment?.view?.parent as? ViewGroup?)?.id == fragmentHolder) {
                fragmentTransaction.hide(addedFragment)

                if (addedFragment is ParentFragment<*>) {
                    hiddenFragments.add(addedFragment)
                }
            }
        }
    }

    if (shouldShow) {
        fragmentTransaction.show(fragment)
    } else {
        fragmentTransaction.add(fragmentHolder, fragment, fragment.identifier)
    }

    fragmentTransaction.runOnCommit {
        hiddenFragments.forEach { it.onFragmentHidden() }
        fragment.onFragmentShown()
        onFragmentShown?.invoke()
    }

    fragmentTransaction.commit()
}

fun createClickableImageView(context: Context, drawable: Drawable?, action: ((View) -> (Unit))?): ImageView {
    return ImageView(context).apply {
        setImageDrawable(drawable)

        adjustViewBounds = true
        scaleType = ImageView.ScaleType.CENTER_INSIDE

        val padding = 12.convertDensityPointsToPixels(context)
        setPadding(padding, padding, padding, padding)

        if (action != null) {

            setSafeOnClickListener {
                action(it)
            }

            with(TypedValue()) {
                context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
                setBackgroundResource(resourceId)
            }
        }
    }
}

fun createClickableImageView(context: Context, drawableResId: Int, action: ((View) -> (Unit))?): ImageView {
    return createClickableImageView(context, ContextCompat.getDrawable(context, drawableResId), action)
}

fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                block()
            }
        }
    })
}

fun View.animateVisibility(state: Int, duration: Long = 300) {
    val aplhaValue = if (state == VISIBLE) 1f else 0f

    val animation = this.animate()
    animation
        .alpha(aplhaValue)
        .duration = duration
    animation.setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {
            if (state == VISIBLE) {
                this@animateVisibility.visibility = state
            }
        }
        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) {
            if (state != VISIBLE) {
                this@animateVisibility.visibility = state
            }
        }
    })
}