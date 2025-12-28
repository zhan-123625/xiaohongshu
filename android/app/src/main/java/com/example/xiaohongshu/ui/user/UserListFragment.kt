package com.example.xiaohongshu.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaohongshu.databinding.FragmentUserListBinding
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.fragment.findNavController
import com.example.xiaohongshu.R

@AndroidEntryPoint
class UserListFragment : Fragment() {

    companion object {
        const val ARG_TYPE = "type" // 0: Following, 1: Followers
        const val ARG_USER_ID = "userId"
    }

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getInt(ARG_TYPE) ?: 0
        val userId = arguments?.getLong(ARG_USER_ID) ?: 0L

        binding.toolbar.title = if (type == 0) "关注" else "粉丝"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = UserAdapter(
            listType = if (type == 0) "following" else "followers",
            onItemClick = { user ->
                // Navigate to profile
            },
            onMessageClick = { user ->
                val bundle = Bundle().apply {
                    putString("username", user.username)
                    putLong("userId", user.id)
                }
                findNavController().navigate(R.id.chatFragment, bundle)
            },
            onActionClick = { user ->
                if (type == 0) {
                    viewModel.unfollowUser(user.id, userId)
                } else {
                    viewModel.blockUser(user.id, userId)
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        if (type == 0) {
            viewModel.loadFollowing(userId)
        } else {
            viewModel.loadFollowers(userId)
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
