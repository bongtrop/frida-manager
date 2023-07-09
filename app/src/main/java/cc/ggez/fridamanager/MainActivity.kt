package cc.ggez.fridamanager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cc.ggez.fridamanager.databinding.ActivityMainBinding
import cc.ggez.fridamanager.model.GithubCommit
import cc.ggez.fridamanager.model.GithubRelease
import cc.ggez.fridamanager.model.GithubTag
import cc.ggez.fridamanager.model.RowItem
import cc.ggez.fridamanager.model.RowItemState
import cc.ggez.fridamanager.util.CommonHelper.Companion.downloadFile
import cc.ggez.fridamanager.util.FridaHelper.Companion.checkFridaServerProcess
import cc.ggez.fridamanager.util.FridaHelper.Companion.getDownloadUrl
import cc.ggez.fridamanager.util.FridaHelper.Companion.getDownloadedFridaTags
import cc.ggez.fridamanager.util.FridaHelper.Companion.removeFridaServer
import cc.ggez.fridamanager.util.FridaHelper.Companion.startFridaServer
import cc.ggez.fridamanager.util.FridaHelper.Companion.stopFridaServer
import cc.ggez.fridamanager.util.GithubHelper
import cc.ggez.fridamanager.util.GithubHelper.Companion.fetchFridaRelease
import cc.ggez.fridamanager.util.GithubHelper.Companion.fetchFridaTags
import cc.ggez.fridamanager.util.GithubHelper.Companion.translateTags
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.DownloadListener2
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import org.tukaani.xz.XZInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    val rvAdapter = RvAdapter()
    lateinit var fridaServerDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        fridaServerDir = File(filesDir, "server")

        rvAdapter.onPowerClick = { position, item ->
            val executingTag = checkFridaServerProcess()
            Log.d(TAG, executingTag)
            if (executingTag.isEmpty() && item.state == RowItemState.INSTALLED) {
                startFridaServer(fridaServerDir.absolutePath, item.tag.name)
                item.state = RowItemState.EXECUTING
            } else if (executingTag == item.tag.name && item.state == RowItemState.EXECUTING) {
                stopFridaServer(item.tag.name)
                item.state = RowItemState.INSTALLED
            } else if (executingTag != item.tag.name && item.state == RowItemState.INSTALLED) {
                Toast.makeText(this, "Another frida-server is running", Toast.LENGTH_SHORT).show()
            }
            rvAdapter.notifyChange()

        }
        rvAdapter.onContainerLongClick = { position, item ->
            if (item.state > RowItemState.NOT_INSTALL) {
                removeFridaServer(fridaServerDir.absolutePath, item.tag.name)
                item.state = RowItemState.NOT_INSTALL
                rvAdapter.notifyChange()

            }
            true

        }
        rvAdapter.onContainerClick = { position, item ->
            if (item.state == RowItemState.NOT_INSTALL) {
                item.state = RowItemState.INSTALLING
                rvAdapter.notifyChange()

                fetchFridaRelease("frida", "frida", item.tag.name, object: GithubHelper.GithubReleaseCallback {
                    override fun onSuccess(release: GithubRelease, resJson: String) {
                        val url = getDownloadUrl(release)
                        downloadFile(url, this@MainActivity.cacheDir, "frida-server-${item.tag.name}.xz", object:
                            DownloadListener1() {

                            override fun taskStart(
                                task: DownloadTask,
                                model: Listener1Assist.Listener1Model
                            ) {
                                Log.d(TAG, "[+] Download Start")
                            }

                            override fun taskEnd(
                                task: DownloadTask,
                                cause: EndCause,
                                realCause: java.lang.Exception?,
                                model: Listener1Assist.Listener1Model
                            ) {
                                if (cause == EndCause.COMPLETED) {
                                    Log.d(TAG, "[+] Download Finish")
                                    if (!fridaServerDir.exists()) {
                                        fridaServerDir.mkdir()
                                    }

                                    Log.d(TAG, "[+] Uncompressing XZ")
                                    try {
                                        val fin =
                                            FileInputStream(
                                                File(
                                                    cacheDir,
                                                    "frida-server-${item.tag.name}.xz"
                                                )
                                            )
                                        val `in` = BufferedInputStream(fin)
                                        val out =
                                            FileOutputStream(
                                                File(
                                                    fridaServerDir,
                                                    "frida-server-${item.tag.name}"
                                                )
                                            )

                                        val xzIn = XZInputStream(`in`)
                                        val buffer = ByteArray(8192)

                                        var n = 0
                                        while (-1 != xzIn.read(buffer).also { n = it }) {
                                            out.write(buffer, 0, n)
                                        }
                                        xzIn.close()
                                        fin.close()
                                        out.close()
                                    } catch (e: IOException) {
                                        Log.e(TAG, "Uncompress XZ Error: ${e.message}")
                                    }

                                    val cacheXZ = File(cacheDir, "frida-server-${item.tag.name}.xz")
                                    if (cacheXZ.exists()) {
                                        cacheXZ.delete()
                                    }

                                    runOnUiThread {
                                        item.state = RowItemState.INSTALLED
                                        rvAdapter.notifyChange()
                                    }
                                } else {
                                    Log.e(TAG, "Download Error: ${cause.name}\n${realCause?.message}")
                                    runOnUiThread {
                                        val message = realCause?.message ?: "Timeout"
                                        Toast.makeText(this@MainActivity, "Download Error: ${cause.name}\n${message}", Toast.LENGTH_LONG).show()
                                        item.state = RowItemState.NOT_INSTALL
                                        rvAdapter.notifyChange()
                                    }
                                }
                            }

                            override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
                                Log.d(TAG, "[+] Download Retry")
                            }

                            override fun connected(
                                task: DownloadTask,
                                blockCount: Int,
                                currentOffset: Long,
                                totalLength: Long
                            ) {
                                Log.d(TAG, "[+] Download Connected")
                            }

                            override fun progress(
                                task: DownloadTask,
                                currentOffset: Long,
                                totalLength: Long
                            ) {
                                Log.d(TAG, "[+] Download Progress: $currentOffset/$totalLength")
                            }
                        })
                    }

                    override fun onFailure(e: IOException) {
                        Log.e(TAG, "Fetch Github Release Error: ${e.message}")
                        runOnUiThread {
                            item.state = RowItemState.NOT_INSTALL
                            rvAdapter.notifyChange()
                        }
                    }
                })
            }
        }

        binding.recyclerView.adapter = rvAdapter

        binding.swipeContainer.setOnRefreshListener {
            fetchGithubTagsAsync(1)
        }

        val tagsCache = File(cacheDir, "tags-cache.json")
        if (tagsCache.exists()) {
            Log.d(TAG, "[+] Read tags from cache")
            val tags = translateTags(tagsCache.readText())
            val installedTags = getDownloadedFridaTags(fridaServerDir.absolutePath)
            val executingTag = checkFridaServerProcess()
            rvAdapter.items.clear()
            rvAdapter.items.addAll(tags.map {
                val rowItem = RowItem(it)
                if (installedTags.contains(it.name)) {
                    rowItem.state = RowItemState.INSTALLED
                }
                if (executingTag == it.name) {
                    rowItem.state = RowItemState.EXECUTING
                }
                rowItem
            })
            rvAdapter.notifyChange()
        } else {
            fetchGithubTagsAsync(1)
        }
    }



    private fun fetchGithubTagsAsync(page: Int) {
        binding.swipeContainer.isRefreshing = true
        fetchFridaTags("frida", "frida", page, object: GithubHelper.GithubTagsCallback {
            override fun onSuccess(tags: List<GithubTag>, resJson: String) {
                val tagsCache = File(cacheDir, "tags-cache.json")
                tagsCache.writeText(resJson)
                val installedTags = getDownloadedFridaTags(fridaServerDir.absolutePath)
                val executingTag = checkFridaServerProcess()
                runOnUiThread {
                    rvAdapter.items.clear()
                    rvAdapter.items.addAll(tags.map {
                        val rowItem = RowItem(it)
                        if (installedTags.contains(it.name)) {
                            rowItem.state = RowItemState.INSTALLED
                        }
                        if (executingTag == it.name) {
                            rowItem.state = RowItemState.EXECUTING
                        }
                        rowItem
                    })
                    rvAdapter.notifyChange()
                }

                binding.swipeContainer.isRefreshing = false
            }

            override fun onFailure(e: IOException) {
                Log.e(TAG, "Fetch Github Tag Error: ${e.message}")
                val installedTags = getDownloadedFridaTags(fridaServerDir.absolutePath)
                val tags = installedTags.map { GithubTag(it, GithubCommit("", ""), "", "", "") }
                runOnUiThread {
                    rvAdapter.items.clear()
                    rvAdapter.items.addAll(tags.map {
                        val rowItem = RowItem(it)
                        rowItem.state = RowItemState.INSTALLED
                        rowItem
                    })
                    rvAdapter.notifyChange()
                }
                binding.swipeContainer.isRefreshing = false
            }
        })
    }

}