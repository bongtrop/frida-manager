package cc.ggez.fridamanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cc.ggez.fridamanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)


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