package com.anantmittal.meraki.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anantmittal.meraki.OwnerData
import com.anantmittal.meraki.R
import com.anantmittal.meraki.RetrofitBuilder
import com.anantmittal.meraki.adapters.WallpaperAdapter
import com.anantmittal.meraki.WallpaperData
import com.anantmittal.meraki.WallpaperDataItem
import com.anantmittal.meraki.databinding.FragmentWallpapersBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Wallpapers : Fragment() {

    private lateinit var binding: FragmentWallpapersBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private val wallpaperList = mutableListOf<WallpaperDataItem>()
    private val list = mutableListOf<OwnerData>()
    private var currentPage = 1
    private var isSearching = false
    private var isLoading = false


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
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        wallpaperAdapter = WallpaperAdapter(requireContext(), wallpaperList) { data ->
            val ownerData = OwnerData(
                Uri.parse(data.urls.raw), data.user.username, data.user.profile_image.large
            )
            list.add(ownerData)
            val bundle = Bundle().apply {
                putSerializable("data", ownerData)
            }
            findNavController().navigate(R.id.action_wallP_to_setWallpaper, bundle)
        }
        binding.profilePicture.setOnClickListener {
            findNavController().navigate(R.id.action_wallP_to_profile)
        }
        recyclerView.adapter = wallpaperAdapter

        fetchWallpapers(currentPage)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isSearching) {
                        isSearching = false
                        binding.searchWallpaper.text?.clear()
                        wallpaperList.clear()
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

        binding.searchWallpaper.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchWallpaper.text.toString().trim()
                if (query.isNotEmpty()) {
                    isSearching = true
                    searchWallpapers(1, query)
                } else {
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

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
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
        isLoading = true
        RetrofitBuilder.instance.searchData(query, page, 30, "portrait")
            .enqueue(object : Callback<WallpaperData> {
                override fun onResponse(
                    call: Call<WallpaperData>, response: Response<WallpaperData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val searchResult = response.body()!!.results
                        if (page == 1) {
                            wallpaperList.clear()
                        }
                        wallpaperList.addAll(searchResult)
                        isLoading = false
                        wallpaperAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<WallpaperData>, t: Throwable) {
                    t.printStackTrace()
                    isLoading = false
                }
            })
    }

    private fun fetchWallpapers(page: Int) {
        isLoading = true
        RetrofitBuilder.instance.data(page, 30, "portrait")
            .enqueue(object : Callback<List<WallpaperDataItem>> {
                override fun onResponse(
                    call: Call<List<WallpaperDataItem>>, response: Response<List<WallpaperDataItem>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val photoList = response.body()!!
//                        wallpaperList.clear()
                        wallpaperList.addAll(photoList)
                        isLoading = false
                        wallpaperAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "response nhi aaya", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<List<WallpaperDataItem>>, t: Throwable) {
                    t.printStackTrace()
                    isLoading = false
                }

            })
    }

}
