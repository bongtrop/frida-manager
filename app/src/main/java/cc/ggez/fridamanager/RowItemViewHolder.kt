package cc.ggez.fridamanager

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ggez.fridamanager.databinding.RowItemBinding
import cc.ggez.fridamanager.model.RowItem
import cc.ggez.fridamanager.model.RowItemState

class RowItemViewHolder(
    private val itemBinding: RowItemBinding
): ViewHolder(
    itemBinding.root
){

    companion object {
        fun create(parent: ViewGroup): RowItemViewHolder {
            return RowItemViewHolder(
                RowItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(
        position: Int,
        item: RowItem,
        onPowerClick: (Int, RowItem) -> Unit,
        onContainerClick: (Int, RowItem) -> Unit,
        onContainerLongClick: (Int, RowItem) -> Boolean
    ) {
        itemBinding.apply {
            tvTitle.text = "Frida Server ${item.tag.name}"
            tvDesc.text = "State: ${item.state}"

            if (item.state == RowItemState.NOT_INSTALL) {
                tvConainer.alpha = 0.6f
                imvPower.visibility = android.view.View.GONE
                pbDownloading.visibility = android.view.View.GONE
                imvActive.visibility = android.view.View.GONE
            }
            else if (item.state == RowItemState.INSTALLING) {
                tvConainer.alpha = 1.0f
                imvPower.visibility = android.view.View.GONE
                pbDownloading.visibility = android.view.View.VISIBLE
                imvActive.visibility = android.view.View.GONE
            }
            else if (item.state == RowItemState.INSTALLED) {
                tvConainer.alpha = 1.0f
                imvPower.visibility = android.view.View.VISIBLE
                pbDownloading.visibility = android.view.View.GONE
                imvActive.visibility = android.view.View.GONE
            }
            else if (item.state == RowItemState.EXECUTING) {
                tvConainer.alpha = 1.0f
                imvPower.visibility = android.view.View.VISIBLE
                pbDownloading.visibility = android.view.View.GONE
                imvActive.visibility = android.view.View.VISIBLE
            }

            imvPower.setOnClickListener {
                onPowerClick.invoke(position, item)
            }

            tvConainer.setOnClickListener {
                onContainerClick.invoke(position, item)
            }
            tvConainer.setOnLongClickListener {
                onContainerLongClick.invoke(position, item)
            }
        }
    }

}