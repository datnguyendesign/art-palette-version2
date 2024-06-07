package com.example.artpaletteversion2.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.artpaletteversion2.R
import com.example.artpaletteversion2.adapter.ExploreAdapter
import com.example.artpaletteversion2.api.ApiUtilities
import com.example.artpaletteversion2.databinding.ActivityExploreBinding
import com.example.artpaletteversion2.model.SearchModel
import com.example.artpaletteversion2.model.UrlImageModel
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExploreBinding

    private var recyclerViewExplore: RecyclerView? = null
    private var list: ArrayList<UrlImageModel>? = null
    private var manager: StaggeredGridLayoutManager? = null
    //    private var manager: GridLayoutManager? = null
    private var adapter: ExploreAdapter? = null
    private var page = 1
    private var dialog: ProgressDialog? = null

    private var pageSize = 30
    private var isLoading: Boolean? = null
    private var isLastPage: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewExplore = findViewById(R.id.recyclerviewExplore)
        list = ArrayList()
        adapter = ExploreAdapter(list!!, this)
        manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        recyclerViewExplore?.layoutManager = manager
        recyclerViewExplore?.setHasFixedSize(true)
        recyclerViewExplore?.adapter = adapter

        dialog = ProgressDialog(this)
        dialog?.setMessage("Loading...")
        dialog?.setCancelable(false)
        dialog?.show()

        getData()

        recyclerViewExplore?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItem = manager?.childCount
                val totalItem = manager?.itemCount
                val firstVisibleItemPositions = manager?.spanCount?.let { IntArray(it) }
                manager?.findFirstVisibleItemPositions(firstVisibleItemPositions)

                val firstVisibleItemPos = firstVisibleItemPositions?.minOrNull() ?: 0

                if (!isLoading!! && !isLastPage!!) {
                    if ((visibleItem!! + firstVisibleItemPos >= totalItem!!) && firstVisibleItemPos >= 0 && totalItem >= pageSize) {
                        page++
                        getData()
                    }

                }
            }
        })

        val menu = findViewById<ChipNavigationBar>(R.id.menu)
        menu.setItemSelected(R.id.explore)
        menu.setOnItemSelectedListener {
            if (it == R.id.add) {
                startActivity(Intent(this@ExploreActivity, AddImageActivity::class.java))
                this@ExploreActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.home) {
                startActivity(Intent(this@ExploreActivity, MainActivity::class.java))
                this@ExploreActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.saved) {
                startActivity(Intent(this@ExploreActivity, SavedActivity::class.java))
                this@ExploreActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
            if (it == R.id.account) {
                startActivity(Intent(this@ExploreActivity, ProfileActivity::class.java))
                this@ExploreActivity.overridePendingTransition(
                    R.anim.animate_fade_enter,
                    R.anim.animate_fade_exit
                )
            }
        }
    }

    private fun getData() {
        isLoading = true
        ApiUtilities.getApiInterface()?.getImages(page, 30)
            ?.enqueue(object : Callback<List<UrlImageModel>?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<UrlImageModel>?>,
                    response: Response<List<UrlImageModel>?>
                ) {
                    if (response.body() != null) {
                        list?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                    }
                    isLoading = false
                    dialog?.dismiss()

                    if (list?.size!! > 0) {
                        isLastPage = list?.size!! < pageSize
                    } else isLastPage = true
                }

                override fun onFailure(call: Call<List<UrlImageModel>?>, t: Throwable) {
                    dialog?.dismiss()
                    Toast.makeText(this@ExploreActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun searchData(query: String?) {
        dialog?.dismiss()
        if (query != null) {
            ApiUtilities.getApiInterface()?.searchImage(query)?.enqueue(object : Callback<SearchModel> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<SearchModel>, response: Response<SearchModel>) {
                    list?.clear()
                    response.body()?.let { list?.addAll(it.results) }
                    adapter?.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}