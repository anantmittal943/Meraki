package com.anantmittal.meraki.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anantmittal.meraki.R
import com.anantmittal.meraki.adapters.WallpaperAdapter
import com.anantmittal.meraki.api.RetrofitBuilder
import com.anantmittal.meraki.api.api_data_modals.WallpaperData
import com.anantmittal.meraki.api.api_data_modals.WallpaperDataItem
import com.anantmittal.meraki.databinding.FragmentWallpapersBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Wallpapers : Fragment() {

    private lateinit var binding: FragmentWallpapersBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private val wallpaperList = mutableListOf<WallpaperDataItem>()
    private var currentPage = 1
    private var isSearching = false
    private var isLoading = false
    private var activeSearchCall: Call<WallpaperData>? = null
    private var activeWallpapersCall: Call<List<WallpaperDataItem>>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWallpapersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(20)
        wallpaperAdapter = WallpaperAdapter(requireContext(), wallpaperList) { data ->
            val bundle = Bundle().apply {
                putString(SetWallpaper.ARG_IMAGE_URI, data.urls.raw)
                putString(SetWallpaper.ARG_OWNER_USERNAME, data.user.username)
                putString(SetWallpaper.ARG_OWNER_PROFILE_URL, data.user.profile_image.large)
            }
            findNavController().navigate(R.id.action_wallP_to_setWallpaper, bundle)
        }
        binding.profilePicture.setOnClickListener {
            findNavController().navigate(R.id.action_wallP_to_profile)
        }
        recyclerView.adapter = wallpaperAdapter

        fetchWallpapers(currentPage)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isSearching) {
                        isSearching = false
                        currentPage = 1
                        binding.searchWallpaper.setText("")
                        fetchWallpapers(1)
                    } else {
                        if (findNavController().currentDestination?.id == R.id.wallP) {
                            activity?.finishAffinity()
                        } else {
                            findNavController().popBackStack()
                        }
                    }
                }
            })

        binding.searchWallpaper.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchWallpaper.text.toString().trim()
                currentPage = 1
                if (query.isNotEmpty()) {
                    isSearching = true
                    searchWallpapers(1, query)
                } else {
                    isSearching = false
                    fetchWallpapers(1)
                }

                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchWallpaper.windowToken, 0)

                true
            } else false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val threshold = 6

                if (dy > 0 && !isLoading && (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold) && firstVisibleItemPosition >= 0) {
                    currentPage++
                    if (isSearching) {
                        val query = binding.searchWallpaper.text.toString().trim()
                        if (query.isNotEmpty()) {
                            searchWallpapers(currentPage, query)
                        } else {
                            fetchWallpapers(1)
                        }
                    } else {
                        fetchWallpapers(currentPage)
                    }
                }
            }

            /*override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }*/
        })

    }

    private fun searchWallpapers(page: Int, query: String) {
        activeSearchCall?.cancel()
        activeWallpapersCall?.cancel()
        isLoading = true
        activeSearchCall = RetrofitBuilder.instance.searchData(query, page, 30, "portrait")
        activeSearchCall?.enqueue(object : Callback<WallpaperData> {
                override fun onResponse(
                    call: Call<WallpaperData>, response: Response<WallpaperData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val searchResult = response.body()!!.results
                        updateWallpaperList(searchResult, page == 1)
                    } else {
                        if (page > 1) currentPage--
                        Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<WallpaperData>, t: Throwable) {
                    if (call.isCanceled) return
                    if (page > 1) currentPage--
                    isLoading = false
                }
            })
    }

    private fun fetchWallpapers(page: Int) {
        activeWallpapersCall?.cancel()
        activeSearchCall?.cancel()
        isLoading = true
        activeWallpapersCall = RetrofitBuilder.instance.data(page, 30, "portrait")
        activeWallpapersCall?.enqueue(object : Callback<List<WallpaperDataItem>> {
                override fun onResponse(
                    call: Call<List<WallpaperDataItem>>, response: Response<List<WallpaperDataItem>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val photoList = response.body()!!
                        updateWallpaperList(photoList, page == 1)
                    } else {
                        if (page > 1) currentPage--
                        Toast.makeText(context, "response nhi aaya", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<List<WallpaperDataItem>>, t: Throwable) {
                    if (call.isCanceled) return
                    if (page > 1) currentPage--
                    isLoading = false
                }

            })
    }

    private fun updateWallpaperList(items: List<WallpaperDataItem>, reset: Boolean) {
        if (reset) {
            val oldSize = wallpaperList.size
            wallpaperList.clear()
            if (oldSize > 0) {
                wallpaperAdapter.notifyItemRangeRemoved(0, oldSize)
            }
        }

        if (items.isEmpty()) return

        val insertStart = wallpaperList.size
        wallpaperList.addAll(items)
        wallpaperAdapter.notifyItemRangeInserted(insertStart, items.size)
    }

    override fun onDestroyView() {
        activeSearchCall?.cancel()
        activeWallpapersCall?.cancel()
        super.onDestroyView()
    }

}
