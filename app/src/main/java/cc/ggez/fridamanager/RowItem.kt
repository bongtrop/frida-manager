package cc.ggez.fridamanager

data class RowItem(
    val title: String,
    val subtitle: String,
    val url: String,
    val isPlaying: Boolean = false,
    val isDownloaded: Boolean = false,
)