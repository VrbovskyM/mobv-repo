package com.example.mobv

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment(R.layout.fragment_feed) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.feed_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val feedAdapter = FeedAdapter()
        recyclerView.adapter = feedAdapter

        feedAdapter.updateItems(listOf(
            MyItem(0,R.drawable.zara1,"zara1"),
            MyItem(1,R.drawable.zara2,"zara2"),
            MyItem(2,R.drawable.zara3,"zara3"),
            MyItem(3,R.drawable.zara4,"zara4"),
            MyItem(4,R.drawable.zara5,"zara5"),
            MyItem(5,R.drawable.zara6,"zara6"),
            MyItem(6,R.drawable.zara7,"zara7"),
            MyItem(7,R.drawable.zara8,"zara8"),
            MyItem(8,R.drawable.zara9,"zara9"),
            MyItem(9,R.drawable.zara10,"zara10"),
        ))
    }
}