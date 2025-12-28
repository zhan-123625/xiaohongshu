package com.example.xiaohongshu.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaohongshu.R
import com.example.xiaohongshu.databinding.FragmentMessageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MessageViewModel by viewModels()
    private lateinit var adapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnLikesCollections.setOnClickListener {
            val user = viewModel.currentUser.value
            if (user != null) {
                val bundle = Bundle().apply {
                    putLong("userId", user.id)
                }
                findNavController().navigate(R.id.interactionListFragment, bundle)
            }
        }

        binding.btnNewFollowers.setOnClickListener {
            val user = viewModel.currentUser.value
            if (user != null) {
                val bundle = Bundle().apply {
                    putInt("type", 1) // Followers
                    putLong("userId", user.id)
                }
                findNavController().navigate(R.id.userListFragment, bundle)
            }
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                adapter = ConversationAdapter(user.id) { otherUserId, username ->
                    val bundle = Bundle().apply {
                        putLong("userId", otherUserId)
                        putString("username", username)
                    }
                    findNavController().navigate(R.id.chatFragment, bundle)
                }
                binding.recyclerView.adapter = adapter
            }
        }

        viewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            if (::adapter.isInitialized) {
                adapter.submitList(conversations)
                binding.tvEmpty.visibility = if (conversations.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        // Refresh profile and conversations
        viewModel.loadProfile()
        viewModel.loadConversations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
