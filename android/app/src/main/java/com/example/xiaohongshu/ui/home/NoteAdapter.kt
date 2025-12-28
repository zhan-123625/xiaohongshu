package com.example.xiaohongshu.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.databinding.ItemNoteBinding

class NoteAdapter(private val onItemClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(note: Note) {
            binding.tvTitle.text = note.title
            // Fix for NullPointerException: Check if user is null
            if (note.user != null) {
                binding.tvUsername.text = note.user.username
                Glide.with(binding.root)
                    .load(note.user.avatarUrl)
                    .circleCrop()
                    .into(binding.ivAvatar)
            } else {
                binding.tvUsername.text = "Unknown User"
                binding.ivAvatar.setImageResource(com.example.xiaohongshu.R.mipmap.ic_launcher_round)
            }
            
            binding.tvCollectCount.text = note.likeCount.toString()

            Glide.with(binding.root)
                .load(note.coverUrl)
                .into(binding.ivCover)
                
            binding.layoutCollect.setOnClickListener {
                // Handle collection click
                // For now just animate or toast
                // In real app, call API
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
