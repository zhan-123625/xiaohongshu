package com.example.xiaohongshu.ui.interaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaohongshu.data.model.Interaction
import com.example.xiaohongshu.databinding.ItemInteractionBinding

class InteractionAdapter(private val type: Int, private val onItemClick: (Interaction) -> Unit) : // 0: Like, 1: Collection
    ListAdapter<Interaction, InteractionAdapter.InteractionViewHolder>(InteractionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InteractionViewHolder {
        val binding = ItemInteractionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InteractionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InteractionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InteractionViewHolder(private val binding: ItemInteractionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(interaction: Interaction) {
            binding.tvUsername.text = interaction.user.username
            binding.tvAction.text = if (type == 0) "赞了你的笔记" else "收藏了你的笔记"
            binding.tvTime.text = interaction.createdAt.substring(0, 10) // Simple date formatting
            
            Glide.with(binding.root)
                .load(interaction.user.avatarUrl)
                .circleCrop()
                .into(binding.ivAvatar)
        }
    }

    class InteractionDiffCallback : DiffUtil.ItemCallback<Interaction>() {
        override fun areItemsTheSame(oldItem: Interaction, newItem: Interaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Interaction, newItem: Interaction): Boolean {
            return oldItem == newItem
        }
    }
}
