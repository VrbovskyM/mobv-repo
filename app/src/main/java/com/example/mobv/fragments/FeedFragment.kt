package com.example.mobv.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.adapters.FeedAdapter
import com.example.mobv.adapters.MyItem
import com.example.mobv.customLayouts.BottomBarLayout
import com.example.mobv.data.DataRepository
import com.example.mobv.databinding.FragmentFeedBinding
import com.example.mobv.viewModels.FeedViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var feedViewModel: FeedViewModel
    private var feedBinding: FragmentFeedBinding? = null
    private var feedItemBinding: FragmentFeedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        feedViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.feed_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)


        feedBinding = FragmentFeedBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            feedModel = feedModel
            feedFragment  = this@FeedFragment
        }

        val feedAdapter = FeedAdapter()

        feedBinding!!.feedRecyclerview.layoutManager = LinearLayoutManager(context)
        feedBinding!!.feedRecyclerview.adapter = feedAdapter

        // Pozorovanie zmeny hodnoty
        feedViewModel.feed_items.observe(viewLifecycleOwner) { items ->
            Log.d("FeedFragment", "nove hodnoty $items")
            feedAdapter.updateItems(items ?: emptyList())
        }

//        feedBinding.pullRefresh.setOnRefreshListener {
//            feedViewModel.updateItems()
//            }
//
//        feedViewModel.loading.observe(viewLifecycleOwner) {
//            feedBinding.pullRefresh.isRefreshing = it
//        }

    }

}