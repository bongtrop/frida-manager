package cc.ggez.fridamanager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cc.ggez.fridamanager.databinding.ActivityMainBinding
import cc.ggez.fridamanager.model.GithubTag
import cc.ggez.fridamanager.model.RowItem
import cc.ggez.fridamanager.model.RowItemState
import cc.ggez.fridamanager.util.GithubHelper
import cc.ggez.fridamanager.util.GithubHelper.Companion.fetchFridaTags
import java.io.IOException

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    val rvAdapter = RvAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        rvAdapter.onPowerClick = { position, item ->
            if (item.state == RowItemState.INSTALLED) {
                item.state = RowItemState.EXECUTING
            } else {
                item.state = RowItemState.INSTALLED
            }
            rvAdapter.notifyChange()

        }
        rvAdapter.onDeleteClick = { position, item ->
            item.state = RowItemState.NOT_INSTALL
            rvAdapter.notifyChange()
        }
        rvAdapter.onContainerClick = { position, item ->
            if (item.state == RowItemState.NOT_INSTALL) {
                item.state = RowItemState.INSTALLED
                rvAdapter.notifyChange()
            }
        }

        binding.recyclerView.adapter = rvAdapter

        binding.swipeContainer.setOnRefreshListener {
            fetchGithubTagsAsync(1)
        }

        fetchGithubTagsAsync(1)
    }

    fun fetchGithubTagsAsync(page: Int) {
        binding.swipeContainer.isRefreshing = true
        fetchFridaTags("frida", "frida", page, object: GithubHelper.GithubTagsCallback {
            override fun onSuccess(tags: List<GithubTag>) {
                Log.d(TAG, tags.toString())
                runOnUiThread {
                    rvAdapter.items.addAll(tags.map { RowItem(it) })
                    rvAdapter.notifyDataSetChanged()
                }

                binding.swipeContainer.isRefreshing = false
            }

            override fun onFailure(e: IOException) {
                Log.e(TAG, "Fetch Github Tag Error: ${e.message}")

                binding.swipeContainer.isRefreshing = false
            }
        })
    }

}