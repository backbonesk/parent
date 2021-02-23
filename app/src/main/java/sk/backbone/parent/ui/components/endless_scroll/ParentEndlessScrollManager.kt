package sk.backbone.parent.ui.components.endless_scroll

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import sk.backbone.parent.execution.ExecutorParams
import sk.backbone.parent.execution.ParentExecutor
import sk.backbone.parent.ui.screens.ParentRecyclerAdapter

open class ParentEndlessScrollManager<Content, Extras, Adapter>(
    protected val recyclerView: RecyclerView,
    protected val executorParams: ExecutorParams,
    protected val adapter: Adapter,
    protected val lifecycleOwner: LifecycleOwner,
    protected val viewModel: IParentEndlessScrollViewModel<Content, Extras>,
    protected val extras: Extras,
    protected val executorFactory: (ExecutorParams) -> ParentExecutor<List<Content>?>,
    protected val swipeToRefresh: SwipeRefreshLayout? = null
) where Adapter : ParentRecyclerAdapter<Content, *> {
    private val context = executorParams.context
    open var merger: Merger<Content>? = null
    var currentLoadingExecutor: ParentExecutor<*>? = null
    var whenNoContent: (() -> Unit)? = null
    @Volatile private var isPerformingFullRefresh = false
    private lateinit var layoutManager: PreCachingLayoutManager

    private val scrollListener by lazy {
        object : EndlessScrollListener(layoutManager) {
            override fun onLoadMore(view: RecyclerView?) {
                loadContent(false)
            }
        }
    }

    open fun setupEndlessScroll() {
        adapter.setHasStableIds(true)
        layoutManager = PreCachingLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(scrollListener)

        viewModel.content.observe(lifecycleOwner, {
            swipeToRefresh?.isRefreshing = false
            adapter.replaceDataSet(it)
            adapter.notifyDataSetChanged()
        })

        swipeToRefresh?.setOnRefreshListener {
            loadContent(true)
        }

        loadContent(true)
    }

    open fun stopEndlessScroll(){
        recyclerView.removeOnScrollListener(scrollListener)
    }

    private fun loadContent(fullRefresh: Boolean = false) {
        currentLoadingExecutor?.uiOperationOnSuccess = null

        if (fullRefresh) {
            scrollListener.resetState()
        }

        swipeToRefresh?.isRefreshing = true

        if (!isPerformingFullRefresh) {
            isPerformingFullRefresh = fullRefresh

            val currentCount = if (fullRefresh) 0 else viewModel.content.value?.size ?: 0

            currentLoadingExecutor = executorFactory(executorParams).apply {
                retryInfinitely = true
                ioOperation = {
                    val contentData = viewModel.provideContent(extras, currentCount)
                    contentData
                }
                uiOperationOnSuccess = { new ->

                    val added: List<Content> = (if (fullRefresh) {
                        mutableListOf()
                    } else {
                        viewModel.content.value ?: listOf()
                    })

                    val merged = merger?.merge(added, new) ?: added + (new ?: listOf())

                    if (isPerformingFullRefresh == fullRefresh) {
                        viewModel.content.value = merged
                    }

                    if (fullRefresh) {
                        isPerformingFullRefresh = false
                    }

                    if(merged.isEmpty()){
                        whenNoContent?.invoke()
                    }
                }

                execute()
            }
        }
    }
}
