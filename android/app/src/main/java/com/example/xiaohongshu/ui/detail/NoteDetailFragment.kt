package com.example.xiaohongshu.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.xiaohongshu.R
import com.example.xiaohongshu.databinding.FragmentNoteDetailBinding
import com.example.xiaohongshu.ui.note.CommentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteDetailViewModel by viewModels()
    private val commentAdapter = CommentAdapter()
    // private val args: NoteDetailFragmentArgs by navArgs() // Assuming SafeArgs is set up, or use Bundle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // For simplicity, we'll just get ID from arguments bundle manually if SafeArgs isn't ready
        val noteId = arguments?.getLong("noteId") ?: return

        viewModel.loadNoteDetail(noteId)
        viewModel.loadComments(noteId)

        setupCommentsRecyclerView()
        observeViewModel()

        // Top Bar Actions
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnFollow.setOnClickListener {
            val note = viewModel.note.value ?: return@setOnClickListener
            note.user?.let { user ->
                viewModel.toggleFollow(user.id)
                // Optimistic update handled by observation or re-fetch, but for immediate feedback:
                // We rely on ViewModel to update the state or just toggle locally
                // Ideally ViewModel should expose isFollowing state separately or update the note object
            }
        }
        
        binding.btnShare.setOnClickListener {
            val note = viewModel.note.value ?: return@setOnClickListener
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, "快来看看这个笔记: ${note.title} \n ${note.content}")
                type = "text/plain"
            }
            startActivity(android.content.Intent.createChooser(shareIntent, "分享到"))
        }

        binding.btnDelete.setOnClickListener {
            android.app.AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("确定要删除这条笔记吗？")
                .setPositiveButton("删除") { _, _ ->
                    viewModel.deleteNote(noteId)
                }
                .setNegativeButton("取消", null)
                .show()
        }

        // Bottom Bar Actions
        binding.btnLike.setOnClickListener {
            viewModel.toggleLike(noteId)
        }
        
        binding.btnCollect.setOnClickListener {
            viewModel.toggleCollect(noteId)
        }
        
        binding.btnCommentScroll.setOnClickListener {
            // Scroll to comments
            binding.rvComments.parent.requestChildFocus(binding.rvComments, binding.rvComments)
        }

        // Comment Input
        binding.etCommentBar.setOnClickListener {
             showCommentDialog(noteId)
        }
    }

    private fun showCommentDialog(noteId: Long) {
        val input = android.widget.EditText(context)
        android.app.AlertDialog.Builder(context)
            .setTitle("发表评论")
            .setView(input)
            .setPositiveButton("发送") { _, _ ->
                val content = input.text.toString()
                if (content.isNotBlank()) {
                    viewModel.postComment(noteId, content)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun setupCommentsRecyclerView() {
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.note.observe(viewLifecycleOwner) { note ->
            binding.tvTitle.text = note.title
            binding.tvContent.text = note.content
            // binding.tvUsername.text = note.user.username // Removed as it's replaced by tvAuthorName
            binding.tvAuthorName.text = note.user?.username ?: "Unknown"
            binding.tvPublishTime.text = "发布于 2025-01-01" // Placeholder, need date in model
            
            binding.tvLikeCount.text = if (note.likeCount > 0) note.likeCount.toString() else "赞"
            binding.tvCollectCountBar.text = if (note.collectionCount > 0) note.collectionCount.toString() else "收藏"
            
            // Update icons based on state
            binding.btnLike.setColorFilter(if (note.isLiked) android.graphics.Color.RED else android.graphics.Color.BLACK)
            binding.btnCollect.setColorFilter(if (note.isCollected) android.graphics.Color.YELLOW else android.graphics.Color.BLACK)

            // Update Follow Button
            if (note.isFollowing) {
                binding.btnFollow.text = "已关注"
                binding.btnFollow.setTextColor(android.graphics.Color.parseColor("#999999"))
                binding.btnFollow.setBackgroundResource(R.drawable.bg_circle_gray) // Need to ensure this drawable exists or use a color
            } else {
                binding.btnFollow.text = "关注"
                binding.btnFollow.setTextColor(android.graphics.Color.parseColor("#FF2442"))
                binding.btnFollow.setBackgroundResource(R.drawable.bg_button_outline)
            }

            Glide.with(this)
                .load(note.coverUrl)
                .into(binding.ivCover)

            // Glide.with(this)
            //    .load(note.user.avatarUrl)
            //    .circleCrop()
            //    .into(binding.ivAvatar) // Removed as it's replaced by ivAuthorAvatar
                
            Glide.with(this)
                .load(note.user?.avatarUrl)
                .circleCrop()
                .into(binding.ivAuthorAvatar)
                
            // Load my avatar placeholder
             Glide.with(this)
                .load(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(binding.ivMyAvatar)
        }

        viewModel.isOwner.observe(viewLifecycleOwner) { isOwner ->
            binding.btnDelete.visibility = if (isOwner) View.VISIBLE else View.GONE
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)
            binding.tvCommentCountBar.text = comments.size.toString()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
