package com.sks225.accessbuddy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sks225.accessbuddy.adapter.BookmarkAdapter
import com.sks225.accessbuddy.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvBookmarks.setItemViewCacheSize(5)
        binding.rvBookmarks.hasFixedSize()
        binding.rvBookmarks.layoutManager = LinearLayoutManager(this)
        binding.rvBookmarks.adapter = BookmarkAdapter(this, isActivity = true)
    }
}