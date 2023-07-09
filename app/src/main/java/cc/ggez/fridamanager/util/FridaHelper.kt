package cc.ggez.fridamanager.util

import android.R.attr
import cc.ggez.fridamanager.model.GithubRelease
import com.topjohnwu.superuser.Shell
import java.io.File


class FridaHelper {
    companion object {

        val archType: String = CommonHelper.getArchType()

        fun checkFridaServerProcess(): String {
            val stdout: List<String> = ArrayList()
            Shell.cmd("ps -A | grep -v grep | grep ggez-server | awk '{print \$9}'").to(stdout).exec()
            if (stdout.isNotEmpty()) {
                return stdout[0].replace(   "ggez-server-", "")
            }
            return ""
        }

        fun startFridaServer(serverPath: String, tag: String) {
            Shell.cmd("cp ${serverPath}/frida-server-${tag} /data/local/tmp/ggez-server-${tag}",
                "chmod +x /data/local/tmp/ggez-server-${tag}",
                "/data/local/tmp/ggez-server-${tag} &"
            ).submit()
        }

        fun stopFridaServer(tag: String) {
            Shell.cmd("killall ggez-server-${tag}").exec()
        }

        fun getDownloadedFridaTags(path: String): List<String> {
            val serverDir = File(path)
            val files = serverDir.listFiles()
            return files?.map { it.name.replace("frida-server-", "") } ?: emptyList()
        }

        fun removeFridaServer(path: String, tag: String) {
            val serverDir = File(path)
            val files = serverDir.listFiles()
            files?.forEach {
                if (it.name.contains(tag)) {
                    it.delete()
                }
            }
        }

        fun getDownloadUrl(release: GithubRelease): String {
            val assets = release.assets
            for (asset in assets) {
                if (asset.name.contains("-$archType") && asset.name.contains("frida-server")) {
                    return asset.browser_download_url
                }
            }
            return ""
        }
    }
}