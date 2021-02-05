package sk.backbone.parent.ui.screens

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ParentRecyclerAdapter<TType, TViewHolder>(protected val context: Context): RecyclerView.Adapter<TViewHolder>() where TViewHolder: ParentRecyclerAdapter.ParentRecyclerViewHolder<TType> {
    protected val dataSet = mutableListOf<TType>()
    abstract override fun getItemId(position: Int): Long

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(viewHolder: TViewHolder, position: Int) {
        viewHolder.bindData(dataSet[position])
    }

    fun replaceDataSet(newDataSet: List<TType>?){
        dataSet.clear()
        newDataSet?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }

    abstract class ParentRecyclerViewHolder<TDataType>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindData(viewData: TDataType)
    }
}