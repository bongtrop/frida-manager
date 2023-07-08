package cc.ggez.fridamanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ggez.fridamanager.databinding.RowItemBinding
import cc.ggez.fridamanager.model.RowItem

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
        item: RowItem,
        onPlayPauseClick: (RowItem) -> Unit,
        onDownloadClick: (RowItem) -> Unit,
    ) {
        itemBinding.apply {
            tvTitle.text = item.title
            tvDesc.text = item.subtitle

            imvPlayPause.setImageDrawable(
                if (item.isPlaying) {
                    imvPlayPause.context.getDrawable(android.R.drawable.ic_media_pause)
                } else {
                    imvPlayPause.context.getDrawable(android.R.drawable.ic_media_play)
                }
            )
            imvDownload.setImageDrawable(
                if (item.isDownloaded) {
                    imvDownload.context.getDrawable(android.R.drawable.ic_lock_idle_alarm)
                } else {
                    imvDownload.context.getDrawable(android.R.drawable.stat_sys_download)
                }
            )
            imvPlayPause.setOnClickListener {
                onPlayPauseClick.invoke(item)
            }

            imvDownload.setOnClickListener {
                onDownloadClick.invoke(item)
            }
        }
    }

}