package com.example.mobv.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobv.R
import com.example.mobv.data.localDb.entities.UserEntity
import com.example.mobv.utils.Utils

data class MyItem(val user: UserEntity) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyItem

        if (user.uid != user.uid) return false
        if (user.photo != user.photo) return false
        if (user.name != user.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.uid.hashCode()
        result = (31 * result) + user.photo.hashCode()
        result = 31 * result + user.name.hashCode()
        return result
    }

}

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var items: List<UserEntity> = listOf()

    // ViewHolder poskytuje odkazy na zobrazenia v každej položke
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Táto metóda vytvára nový ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }
    // Táto metóda prepojí dáta s ViewHolderom
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {

        holder.itemView.findViewById<TextView>(R.id.username_text).text = items[position].name
        holder.itemView.findViewById<TextView>(R.id.last_updated_text).text = items[position].updated
        // TODO< fix loading images >
        // holder.itemView.findViewById<ImageView>(R.id.profile_image).setImageResource(items[position].photo)

    }

    // Vracia počet položiek v zozname
    override fun getItemCount() = items.size

    fun updateItems(newItems: List<UserEntity>) {
        val diffCallback = Utils.ItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}