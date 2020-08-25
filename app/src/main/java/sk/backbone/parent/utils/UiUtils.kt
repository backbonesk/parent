package sk.backbone.parent.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewParent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import sk.backbone.parent.ui.components.SafeClickListener
import sk.backbone.parent.ui.validations.IValidableInput
import java.math.BigDecimal


fun View.setSafeOnClickListener(action: (View) -> Unit) {
    val safeClickListener =
        SafeClickListener {
            action(it)
        }
    setOnClickListener(safeClickListener)
}

fun View.hideKeyboard(){
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun createRecyclerViewHolder(parent: ViewGroup, layoutId: Int) : View{
    return LayoutInflater.from(parent.context).inflate(layoutId, parent,false)
}

fun View.scrollToView(){
    var parent: ViewParent?
    parent = this.parent

    while (parent !is ScrollView && parent != null){
        parent = parent.parent
    }

    if(parent is ScrollView){
        parent.post {
            parent.smoothScrollTo(0, this.top)
        }
    }
    else {
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

fun Context.sendActionToOtherApp(message: String, title: String, subject: String){
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    shareIntent.putExtra(Intent.EXTRA_TEXT, message)

    ContextCompat.startActivity(this, Intent.createChooser(shareIntent, title), null)
}

fun TextView?.setTextAndUpdateVisibility(input: CharSequence?){
    if(input.isNullOrEmpty()){
        this?.visibility = GONE
    }
    else {
        this?.visibility = VISIBLE
    }

    this?.text = input
}

fun ImageView.loadResource(url: String?, defaultImage: Int? = null, options: RequestOptions = RequestOptions().apply{ centerCrop() }){
    Glide.with(this).clear(this)
    Glide.with(this).load(url).apply(options).apply {
        if (defaultImage != null) {
            placeholder(defaultImage).fallback(defaultImage).into(this@loadResource)
        }
        else {
            into(this@loadResource)
        }
    }
}

fun Int.getResourceStringValue(context: Context) : String{
    return context.resources.getString(this)
}

fun BigDecimal.getAsEuros(): String{
    val euroSign = "€"
    return String.format("$this $euroSign")
}

fun <T>ViewGroup.getViewsByType(tClass: Class<T>): ArrayList<T> {
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
    for (input in inputs){

        val isInputValid = input.validate()

        areInputsValid = isInputValid && areInputsValid

        if(input is View && !isInputValid){
            invalidInputs.add(input)
        }
    }

    if(!areInputsValid){
        scrollToFirstView(invalidInputs)
    }

    return areInputsValid
}