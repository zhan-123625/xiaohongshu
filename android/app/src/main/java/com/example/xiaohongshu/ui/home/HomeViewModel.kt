package com.example.xiaohongshu.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xiaohongshu.data.model.Note
import com.example.xiaohongshu.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页 ViewModel
 * 负责加载首页笔记流数据
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    /**
     * 加载推荐笔记流
     * 目前固定加载第一页，每页10条
     */
    fun loadFeed() {
        Log.d(TAG, "loadFeed called")
        viewModelScope.launch {
            try {
                val result = repository.getFeed(1, 10)
                Log.d(TAG, "Feed loaded: ${result.size} items")
                _notes.value = result
            } catch (e: Exception) {
                Log.e(TAG, "Error loading feed", e)
                e.printStackTrace()
            }
        }
    }
}
