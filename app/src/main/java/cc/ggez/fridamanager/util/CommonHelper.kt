package cc.ggez.fridamanager.util

import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.topjohnwu.superuser.Shell
import java.io.File
import java.util.Timer


class CommonHelper {

    companion object {
        fun getArchType(): String {
            val archType = Shell.cmd("getprop ro.product.cpu.abi").exec().out[0]
            return when (archType) {
                "x86" -> "x86"
                "arm64-v8a" -> "arm64"
                "x86_64" -> "x86_64"
                "armeabi-v7a" -> "arm"
                "armeabi" -> "arm"
                else -> "arm64"
            }
        }

        fun downloadFile(url: String, path: File, filename: String, listener: DownloadListener1): DownloadTask {
            val task = DownloadTask.Builder(url, path)

                .setFilename(filename)
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(false)
                .build()

            task.enqueue(listener)
            Timer().schedule(object : java.util.TimerTask() {
                override fun run() {
                    task.cancel()
                }
            }, 20000)
            return task
        }
    }
}