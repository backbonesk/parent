package sk.backbone.parent.ui.components.spinner

class DefaultSpinnerItemsProvider(override val elements: List<String>) : SpinnerItemsProvider<String>(){
    override fun getString(value: String): String {
        return value
    }
}