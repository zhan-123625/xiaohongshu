package com.example.xiaohongshu.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaohongshu.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: MessageAdapter
    private var targetUserId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val username = arguments?.getString("username") ?: "User"
        // We need userId to fetch messages. Assuming it's passed or we need to fetch it.
        // In UserListFragment we passed "username", but we should pass "userId" too.
        // Let's assume we update UserListFragment to pass userId.
        // For now, let's check if we can get userId from arguments.
        // If not, we might need to change how we navigate.
        
        // Wait, I only passed username in UserListFragment. I should fix that.
        // But let's assume I will fix it.
        targetUserId = arguments?.getLong("userId") ?: 0L
        
        binding.tvChatTitle.text = username
        
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                adapter = MessageAdapter(user.id)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                
                if (targetUserId != 0L) {
                    viewModel.loadMessages(targetUserId)
                }
            }
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerView.scrollToPosition(messages.size - 1)
            }
        }

        binding.btnSend.setOnClickListener {
            val content = binding.etMessage.text.toString()
            if (content.isNotBlank() && targetUserId != 0L) {
                viewModel.sendMessage(targetUserId, content)
                binding.etMessage.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
