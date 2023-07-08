package cc.ggez.fridamanager.util

import android.util.Log
import cc.ggez.fridamanager.model.GithubRelease
import cc.ggez.fridamanager.model.GithubTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class GithubHelper {
    interface GithubTagsCallback {
        fun onSuccess(tags: List<GithubTag>)
        fun onFailure(e: IOException)
    }

    interface GithubReleaseCallback {
        fun onSuccess(release: GithubRelease)
        fun onFailure(e: IOException)
    }
    companion object {

        val TAG = "FridaGithubHelper"
        val fridaGithubEndpoint = "https://api.github.com/repos"

        private val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        fun fetchFridaTags(owner: String, repo: String, page: Int, callback: GithubTagsCallback) {
            val urlBuilder: HttpUrl.Builder =
                ("$fridaGithubEndpoint/$owner/$repo/tags").toHttpUrlOrNull()!!.newBuilder()

            urlBuilder.addQueryParameter("per_page", "100")
            urlBuilder.addQueryParameter("page", page.toString())
            val url = urlBuilder.build().toString()

            val request: Request = Request.Builder()
                .url(url)
                .build()

            val call = client.newCall(request)
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "${e.message}")
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val gson = Gson()
                    val listType: Type = object : TypeToken<ArrayList<GithubTag?>?>() {}.type
                    val githubTags: List<GithubTag> = gson.fromJson(response.body!!.string(), listType)
                    callback.onSuccess(githubTags)
                }
            })
        }

        fun fetchFridaRelease(owner: String, repo: String, tag: String, callback: GithubReleaseCallback) {
            val urlBuilder: HttpUrl.Builder =
                ("$fridaGithubEndpoint/$owner/$repo/releases/tags/$tag").toHttpUrlOrNull()!!.newBuilder()

            val url = urlBuilder.build().toString()

            val request: Request = Request.Builder()
                .url(url)
                .build()

            val call = client.newCall(request)
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "${e.message}")
                    callback.onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val gson = Gson()
                    val githubRelease: GithubRelease = gson.fromJson(response.body!!.string(), GithubRelease::class.java)
                    callback.onSuccess(githubRelease)
                }
            })
        }


    }


}