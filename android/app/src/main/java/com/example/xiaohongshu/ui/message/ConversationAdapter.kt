package com.example.xiaohongshu.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaohongshu.data.model.Message
import com.example.xiaohongshu.databinding.ItemConversationBinding

class ConversationAdapter(
    private val currentUserId: Long,
    private val onItemClick: (Long, String) -> Unit
) : ListAdapter<Message, ConversationAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val message = getItem(position)
                    val otherUser = if (message.senderId == currentUserId) message.receiver else message.sender
                    onItemClick(otherUser.id, otherUser.username)
                }
            }
        }

        fun bind(message: Message) {
            val otherUser = if (message.senderId == currentUserId) message.receiver else message.sender
            
            binding.tvUsername.text = otherUser.username
            binding.tvLastMessage.text = message.content
            binding.tvTime.text = message.createdAt.substring(0, 10) // Simple date formatting

            Glide.with(binding.root)
                .load(otherUser.avatarUrl)
                .circleCrop()
                .into(binding.ivAvatar)
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
