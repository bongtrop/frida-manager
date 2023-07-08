package cc.ggez.fridamanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cc.ggez.fridamanager.databinding.ActivityMainBinding
import cc.ggez.fridamanager.model.GithubRelease
import cc.ggez.fridamanager.model.GithubTag
import cc.ggez.fridamanager.model.RowItem
import cc.ggez.fridamanager.util.GithubHelper
import cc.ggez.fridamanager.util.GithubHelper.Companion.fetchFridaRelease
import cc.ggez.fridamanager.util.GithubHelper.Companion.fetchFridaTags
import cc.ggez.fridamanager.util.FridaHelper.Companion.getDownloadUrl
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        fetchFridaTags(object : GithubHelper.GithubTagsCallback {
            override fun onSuccess(tags: List<GithubTag>) {
                Log.d(TAG, "onSuccess: $tags")
            }

            override fun onFailure(e: IOException) {
                println(e)
            }
        })

        fetchFridaRelease("16.1.1", object : GithubHelper.GithubReleaseCallback {
            override fun onSuccess(release: GithubRelease) {
                Log.d(TAG, getDownloadUrl(release))
            }

            override fun onFailure(e: IOException) {
                println(e)
            }
        })

        val items = mutableListOf(
            RowItem(
                title = "Title 1",
                subtitle = "Subtitle 1",
                url = "https://www.google.com",
                isPlaying = false,
                isDownloaded = false,
            ),
            RowItem(
                title = "Title 2",
                subtitle = "Subtitle 2",
                url = "https://www.google.com",
                isPlaying = false,
                isDownloaded = false,
            ),
            RowItem(
                title = "Title 3",
                subtitle = "Subtitle 3",
                url = "https://www.google.com",
                isPlaying = false,
                isDownloaded = false,
            ),
        )

        val rvAdapter = RvAdapter()
        rvAdapter.items.addAll(items)
        rvAdapter.onPlayPauseClick = { item ->

        }
        rvAdapter.onDownloadClick = { item ->

        }
        binding.recyclerView.adapter = rvAdapter

    }
}