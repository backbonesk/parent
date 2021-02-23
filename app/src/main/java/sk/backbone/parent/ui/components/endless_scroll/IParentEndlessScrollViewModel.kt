package sk.backbone.parent.ui.components.endless_scroll

import androidx.lifecycle.MutableLiveData

interface IParentEndlessScrollViewModel<Content, Extras> {
    val content: MutableLiveData<List<Content>>

    suspend fun provideContent(extras: Extras, currentCount: Int): List<Content>?
}