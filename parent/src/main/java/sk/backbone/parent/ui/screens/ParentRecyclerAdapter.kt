package sk.backbone.parent.ui.screens

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.scopes.ActivityScopes
import javax.inject.Inject

abstract class ParentRecyclerAdapter<TType, TViewHolder>(val context: Context): RecyclerView.Adapter<TViewHolder>(), IExecutioner<ActivityScopes> where TViewHolder: ParentRecyclerAdapter.ParentRecyclerViewHolder<TType> {
    @Inject override lateinit var scopes: ActivityScopes

    protected val dataSet = mutableListOf<TType>()
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(viewHolder: TViewHolder, position: Int) {
        viewHolder.bindData(dataSet[position])
    }

    fun replaceDataSet(newDataSet: List<TType>?){
        dataSet.clear()
        newDataSet?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int){
        dataSet.add(to, dataSet.removeAt(from))
        notifyItemMoved(from, to)
    }

    abstract class ParentRecyclerViewHolder<TDataType>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindData(viewData: TDataType)
    }
}