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
        onDeleteClick: (Int, RowItem) -> Unit,
        onContainerClick: (Int, RowItem) -> Unit,
    ) {
        itemBinding.apply {
            tvTitle.text = "Frida Server ${item.tag.name}"
            tvDesc.text = "State: ${item.state}"

            if (item.state == RowItemState.NOT_INSTALL) {
                tvConainer.alpha = 0.6f
                imvPower.visibility = android.view.View.GONE
                imvDelete.visibility = android.view.View.GONE
                pbDownloading.visibility = android.view.View.GONE
            }
            else if (item.state == RowItemState.INSTALLING) {
                tvConainer.alpha = 1.0f

                imvPower.visibility = android.view.View.GONE

                imvDelete.visibility = android.view.View.GONE

                pbDownloading.visibility = android.view.View.VISIBLE
            }
            else if (item.state == RowItemState.INSTALLED) {
                tvConainer.alpha = 1.0f

                imvPower.visibility = android.view.View.VISIBLE
                imvPower.alpha = 0.4f

                imvDelete.visibility = android.view.View.VISIBLE

                pbDownloading.visibility = android.view.View.GONE
            }
            else if (item.state == RowItemState.EXECUTING) {
                tvConainer.alpha = 1.0f

                imvPower.visibility = android.view.View.VISIBLE
                imvPower.alpha = 1.0f

                imvDelete.visibility = android.view.View.GONE

                pbDownloading.visibility = android.view.View.GONE
            }

            imvPower.setOnClickListener {
                onPowerClick.invoke(position, item)
            }

            imvDelete.setOnClickListener {
                onDeleteClick.invoke(position, item)
            }

            tvConainer.setOnClickListener {
                onContainerClick.invoke(position, item)
            }
        }
    }

}