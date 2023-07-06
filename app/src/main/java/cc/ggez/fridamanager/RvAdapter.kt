package cc.ggez.fridamanager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RvAdapter: RecyclerView.Adapter<RowItemViewHolder>() {

    val items = mutableListOf<RowItem>()

    var onPlayPauseClick: (RowItem) -> Unit = {}
    var onDownloadClick: (RowItem) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowItemViewHolder {
        return RowItemViewHolder.create(
            parent
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RowItemViewHolder, position: Int) {
        holder.bind(
            items[position],
            onPlayPauseClick = onPlayPauseClick,
            onDownloadClick = onDownloadClick
        )
    }
}