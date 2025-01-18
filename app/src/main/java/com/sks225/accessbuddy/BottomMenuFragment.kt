package com.sks225.accessbuddy

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sks225.accessbuddy.MainActivity.Companion.bookmarkIndex
import com.sks225.accessbuddy.MainActivity.Companion.bookmarkList
import com.sks225.accessbuddy.MainActivity.Companion.isDesktopSite
import com.sks225.accessbuddy.MainActivity.Companion.tabsList
import com.sks225.accessbuddy.databinding.BookmarkDialogBinding
import com.sks225.accessbuddy.databinding.FragmentBottomMenuBinding
import com.sks225.accessbuddy.databinding.MoreFeaturesBinding
import com.sks225.accessbuddy.model.Bookmark
import java.io.ByteArrayOutputStream

class BottomMenuFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val frag = getCurrentFragment()

        val myView = layoutInflater.inflate(R.layout.more_features, binding.root, false)
        val dialogBinding = MoreFeaturesBinding.bind(view)

        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(myView).create()

        dialog.window?.apply {
            attributes.gravity = Gravity.BOTTOM
            attributes.y = 50
            setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
        }
        dialog.show()

        frag?.let {
            // Bookmark logic
            //bookmarkIndex = isBookmarked(it.binding.webView.url!!)
            if (bookmarkIndex != -1) {
                dialogBinding.bookmarkBtn.apply {
                    setIconTintResource(R.color.cool_blue)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.cool_blue))
                }
            }
        }

        if (isDesktopSite) {
            dialogBinding.desktopBtn.apply {
                setIconTintResource(R.color.cool_blue)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.cool_blue))
            }
        }

        // Handle back button press
        dialogBinding.backBtn.setOnClickListener {
            Log.d("tag", "Back pressed")
            frag?.apply {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                }
            }
        }

        // Handle forward button press
        dialogBinding.forwardBtn.setOnClickListener {
            frag?.apply {
                if (binding.webView.canGoForward()) {
                    binding.webView.goForward()
                }
            }
        }

        // Handle save button press (PDF Save)
        dialogBinding.saveBtn.setOnClickListener {
            dialog.dismiss()
            if (frag != null) {
                // Calling saveAsPdf from MainActivity
                (activity as MainActivity).saveAsPdf(frag.binding.webView)
            } else {
                Snackbar.make(binding.root, "First Open A WebPage\uD83D\uDE03", 3000).show()
            }
        }

        // Handle desktop button press
        dialogBinding.desktopBtn.setOnClickListener {
            it as MaterialButton
            frag?.binding?.webView?.apply {
                isDesktopSite = if (isDesktopSite) {
                    settings.userAgentString = null
                    it.setIconTintResource(R.color.black)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    false
                } else {
                    settings.userAgentString =
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:99.0) Gecko/20100101 Firefox/99.0"
                    settings.useWideViewPort = true
                    evaluateJavascript(
                        "document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));",
                        null
                    )
                    it.setIconTintResource(R.color.cool_blue)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.cool_blue))
                    true
                }
                reload()
                dialog.dismiss()
            }
        }

        // Handle bookmark button press
        dialogBinding.bookmarkBtn.setOnClickListener {
            frag?.let {
                if (bookmarkIndex == -1) {
                    val viewB = layoutInflater.inflate(R.layout.bookmark_dialog, binding.root, false)
                    val bBinding = BookmarkDialogBinding.bind(viewB)
                    val dialogB = MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Add Bookmark")
                        .setMessage("Url: ${it.binding.webView.url}")
                        .setPositiveButton("Add") { self, _ ->
                            try {
                                val array = ByteArrayOutputStream()
                                it.webIcon?.compress(Bitmap.CompressFormat.PNG, 100, array)
                                bookmarkList.add(
                                    Bookmark(
                                        name = bBinding.bookmarkTitle.text.toString(),
                                        url = it.binding.webView.url!!,
                                        array.toByteArray()
                                    )
                                )
                            } catch (e: Exception) {
                                bookmarkList.add(
                                    Bookmark(
                                        name = bBinding.bookmarkTitle.text.toString(),
                                        url = it.binding.webView.url!!
                                    )
                                )
                            }
                            self.dismiss()
                        }
                        .setNegativeButton("Cancel") { self, _ -> self.dismiss() }
                        .setView(viewB).create()
                    dialogB.show()
                    bBinding.bookmarkTitle.setText(it.binding.webView.title)
                } else {
                    val dialogB = MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Remove Bookmark")
                        .setMessage("Url: ${it.binding.webView.url}")
                        .setPositiveButton("Remove") { self, _ ->
                            bookmarkList.removeAt(bookmarkIndex)
                            self.dismiss()
                        }
                        .setNegativeButton("Cancel") { self, _ -> self.dismiss() }
                        .create()
                    dialogB.show()
                }
            }
            dialog.dismiss()
        }
    }
    private fun getCurrentFragment(): BrowseFragment? {
        return try {
            val currentTab = tabsList[binding.myPager.currentItem]
            currentTab.fragment as? BrowseFragment
        } catch (e: Exception) {
            null
        }
    }*/
    }
}