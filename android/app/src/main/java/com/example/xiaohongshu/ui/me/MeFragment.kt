package com.example.xiaohongshu.ui.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.xiaohongshu.databinding.FragmentMeBinding
import com.example.xiaohongshu.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.navigation.fragment.findNavController
import com.example.xiaohongshu.R

@AndroidEntryPoint
class MeFragment : Fragment() {

    private var _binding: FragmentMeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeViewModel by viewModels()

    // @Inject
    // lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        viewModel.loadProfile()

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.username
            binding.tvBio.text = user.bio ?: "暂无简介"
            binding.tvId.text = "小红书号: ${user.id}"
            
            binding.tvFollowingCount.text = (user.followingCount ?: 0).toString()
            binding.tvFollowerCount.text = (user.followerCount ?: 0).toString()
            binding.tvLikeCount.text = (user.likeCount ?: 0).toString()
            
            Glide.with(this)
                .load(user.avatarUrl)
                .circleCrop()
                .into(binding.ivAvatar)
        }

        viewModel.unreadCounts.observe(viewLifecycleOwner) { counts ->
            val followers = counts["followers"] ?: 0
            val likes = counts["likes"] ?: 0
            val collections = counts["collections"] ?: 0
            val totalLikes = likes + collections

            if (followers > 0) {
                binding.tvFollowerBadge.text = followers.toString()
                binding.tvFollowerBadge.visibility = View.VISIBLE
            } else {
                binding.tvFollowerBadge.visibility = View.GONE
            }

            if (totalLikes > 0) {
                binding.tvLikeBadge.text = totalLikes.toString()
                binding.tvLikeBadge.visibility = View.VISIBLE
            } else {
                binding.tvLikeBadge.visibility = View.GONE
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            // Navigate back to login or restart app
            // For now, just finish activity or navigate to login
            activity?.finish()
        }

        binding.llFollowing.setOnClickListener {
            val user = viewModel.user.value
            if (user != null) {
                val bundle = Bundle().apply {
                    putInt("type", 0)
                    putLong("userId", user.id)
                }
                findNavController().navigate(R.id.userListFragment, bundle)
            }
        }

        binding.llFollowers.setOnClickListener {
            val user = viewModel.user.value
            if (user != null) {
                val bundle = Bundle().apply {
                    putInt("type", 1)
                    putLong("userId", user.id)
                }
                findNavController().navigate(R.id.userListFragment, bundle)
            }
        }
        
        binding.llLikes.setOnClickListener {
            val user = viewModel.user.value
            if (user != null) {
                val bundle = Bundle().apply {
                    putLong("userId", user.id)
                }
                findNavController().navigate(R.id.interactionListFragment, bundle)
            }
        }
    }

    private fun setupViewPager() {
        val titles = listOf("笔记", "收藏", "赞过")
        
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = titles.size

            override fun createFragment(position: Int): Fragment {
                return NoteListFragment.newInstance(position)
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
