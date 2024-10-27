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
import com.example.mobv.databinding.FragmentFeedBinding
import com.example.mobv.data.DataRepository
import com.example.mobv.viewModels.FeedViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel
    private var binding: FragmentFeedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter()
            bnd.feedRecyclerview.adapter = feedAdapter

            // Pozorovanie zmeny hodnoty
            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                Log.d("FeedFragment", "nove hodnoty $items")
                feedAdapter.updateItems(items ?: emptyList())
            }

            bnd.pullRefresh.setOnRefreshListener {
                viewModel.updateItems()
            }
            viewModel.loading.observe(viewLifecycleOwner) {
                bnd.pullRefresh.isRefreshing = it
            }

        }
    }
}
//feedAdapter.updateItems(listOf(
//MyItem(0, R.drawable.zara1,"zara1"),
//MyItem(1, R.drawable.zara2,"zara2"),
//MyItem(2, R.drawable.zara3,"zara3"),
//MyItem(3, R.drawable.zara4,"zara4"),
//MyItem(4, R.drawable.zara5,"zara5"),
//MyItem(5, R.drawable.zara6,"zara6"),
//MyItem(6, R.drawable.zara7,"zara7"),
//MyItem(7, R.drawable.zara8,"zara8"),
//MyItem(8, R.drawable.zara9,"zara9"),
//MyItem(9, R.drawable.zara10,"zara10"),
//))