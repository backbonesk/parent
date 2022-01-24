package sk.backbone.parent.ui.components.spinner

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes

class PrentSpinnerAdapter(context: Context, @LayoutRes resource: Int, items: List<String>, private val defaultOption: String?) : ArrayAdapter<String>(context, resource, run { mutableListOf(*items.toTypedArray()).apply { defaultOption?.let { add(it) } } }) {
    override fun getCount(): Int {
        return super.getCount() - (if (defaultOption == null) 0 else  1)
    }
}