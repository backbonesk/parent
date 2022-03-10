package sk.backbone.parent.ui.components.recycler_sticky_header

import android.view.View
import android.view.ViewGroup

interface StickyHeaderInterface<TData, THeaderHolder: ViewGroup> {
    val shouldCreateHeader: (TData, TData) -> Boolean

    fun createHeaderView(position: Int, parent: ViewGroup): View
    fun getItemAtPosition(itemPosition: Int): TData?
    fun getHeaderLayout(view: View): THeaderHolder

    fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        var headerPosition = 0
        do {
            if (this.isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    fun isHeader(itemPosition: Int): Boolean {
        var isHeader = itemPosition == 0

        if(itemPosition != 0){
            val current = getItemAtPosition(itemPosition)
            val previous = getItemAtPosition(itemPosition - 1)

            if(current != null && previous != null && shouldCreateHeader(current, previous)){
                isHeader = true
            }
        }

        return isHeader
    }
}