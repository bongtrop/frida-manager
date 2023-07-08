package cc.ggez.fridamanager.model

enum class RowItemState(s: Int) {
    NOT_INSTALL(0), INSTALLING(1), INSTALLED(2), EXECUTING(3)
}
data class RowItem(
    val tag: GithubTag,
    var state: RowItemState = RowItemState.NOT_INSTALL,
)