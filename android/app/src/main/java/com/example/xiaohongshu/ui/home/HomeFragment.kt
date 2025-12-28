package com.example.xiaohongshu.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.xiaohongshu.R
import com.example.xiaohongshu.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 首页 Fragment
 * 展示笔记瀑布流列表
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 初始化首页")

        setupRecyclerView()
        observeViewModel()

        // 加载初始数据
        viewModel.loadFeed()
        
        // 设置下拉刷新监听
        binding.swipeRefresh.setOnRefreshListener {
            Log.d(TAG, "Refreshing feed")
            viewModel.loadFeed()
        }
    }

    /**
     * 初始化 RecyclerView
     * 设置布局管理器为瀑布流，并配置适配器
     */
    private fun setupRecyclerView() {
        adapter = NoteAdapter { note ->
            Log.d(TAG, "Clicked note: ${note.id}")
            val bundle = Bundle().apply {
                putLong("noteId", note.id)
            }
            findNavController().navigate(R.id.action_homeFragment_to_noteDetailFragment, bundle)
        }
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.adapter = adapter
    }

    /**
     * 观察 ViewModel 数据变化
     */
    private fun observeViewModel() {
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            Log.d(TAG, "Received ${notes.size} notes")
            adapter.submitList(notes)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
