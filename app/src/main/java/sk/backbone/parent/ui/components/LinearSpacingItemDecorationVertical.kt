package sk.backbone.parent.ui.components

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class LinearSpacingItemDecorationVertical(private val spacing: Int) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val isLast = position == state.itemCount - 1

        if (!isLast) {
            outRect.bottom = spacing
        }
    }
}