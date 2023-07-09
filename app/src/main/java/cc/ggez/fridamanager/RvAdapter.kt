package cc.ggez.fridamanager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cc.ggez.fridamanager.model.RowItem

class RvAdapter: RecyclerView.Adapter<RowItemViewHolder>() {

    val items = mutableListOf<RowItem>()

    var onPowerClick: (position: Int, rowItem: RowItem) -> Unit = { position: Int, rowItem: RowItem -> }
    var onContainerClick: (position: Int, rowItem: RowItem) -> Unit = { position: Int, rowItem: RowItem -> }
    var onContainerLongClick: (position: Int, rowItem: RowItem) -> Boolean = { position: Int, rowItem: RowItem -> false }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowItemViewHolder {
        return RowItemViewHolder.create(
            parent
        )
    }

    fun notifyChange() {
        items.sortWith { item1, item2 ->
            if (item1.state == item2.state) {
                item2.tag.name.compareTo(item1.tag.name)
            }
            else {
                item2.state.compareTo(item1.state)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RowItemViewHolder, position: Int) {
        holder.bind(
            position,
            items[position],
            onPowerClick = onPowerClick,
            onContainerClick = onContainerClick,
            onContainerLongClick = onContainerLongClick
        )
    }
}