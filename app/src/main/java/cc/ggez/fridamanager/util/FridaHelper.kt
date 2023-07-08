package cc.ggez.fridamanager.util

import cc.ggez.fridamanager.model.GithubRelease
import com.topjohnwu.superuser.Shell


class FridaHelper {
    companion object {

        val archType: String = CommonHelper.getArchType()

        fun checkFridaServerProcess(): Boolean {
            val stdout: List<String> = ArrayList()
            Shell.cmd("ps -A | grep ggez-server").to(stdout).exec()
            return stdout.isNotEmpty()
        }

        fun startFridaServer() {
            Shell.cmd("/data/local/tmp/ggez-server").exec()
        }

        fun stopFridaServer() {
            Shell.cmd("killall ggez-server").exec()
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