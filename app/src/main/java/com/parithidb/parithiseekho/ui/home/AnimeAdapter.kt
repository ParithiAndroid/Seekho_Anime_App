package com.parithidb.parithiseekho.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parithidb.parithiseekho.R
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity
import com.parithidb.parithiseekho.databinding.ItemAnimeBinding
import com.squareup.picasso.Picasso

class AnimeAdapter(
    private val onClick: (AnimeEntity) -> Unit
) : ListAdapter<AnimeEntity, AnimeAdapter.UserViewHolder>(UserDiffCallback()) {

    class UserDiffCallback : DiffUtil.ItemCallback<AnimeEntity>() {
        override fun areItemsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean {
            return oldItem.animeId == newItem.animeId
        }

        override fun areContentsTheSame(oldItem: AnimeEntity, newItem: AnimeEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeAdapter.UserViewHolder {
        val binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeAdapter.UserViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.tvTitle.text = item.title
            holder.binding.tvRating.text = "${item.score} / 10"
            holder.binding.tvEpisodes.text = "${item.episodes} episodes"
            Picasso.get()
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
                .into(holder.binding.ivPoster)

            holder.binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    inner class UserViewHolder(val binding: ItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root)
}