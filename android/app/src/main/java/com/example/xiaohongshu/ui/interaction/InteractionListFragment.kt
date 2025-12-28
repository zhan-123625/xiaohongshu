package com.example.xiaohongshu.ui.interaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xiaohongshu.databinding.FragmentUserListBinding // Reuse layout
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.fragment.findNavController
import com.example.xiaohongshu.R

@AndroidEntryPoint
class InteractionListFragment : Fragment() {

    companion object {
        const val ARG_USER_ID = "userId"
    }

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InteractionListViewModel by viewModels()
    private lateinit var adapter: InteractionAdapter

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

        val userId = arguments?.getLong(ARG_USER_ID) ?: 0L

        binding.toolbar.title = "获赞与收藏"
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adapter = InteractionAdapter(0) { interaction ->
            val bundle = Bundle().apply {
                putLong("noteId", interaction.noteId)
            }
            findNavController().navigate(R.id.noteDetailFragment, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.loadInteractions(userId)

        viewModel.interactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
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
