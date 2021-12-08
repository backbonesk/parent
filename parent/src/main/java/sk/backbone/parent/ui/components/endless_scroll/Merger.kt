package sk.backbone.parent.ui.components.endless_scroll

interface Merger<Type> {
    fun merge(first: List<Type>?, second: List<Type>?): List<Type>
}