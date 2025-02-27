package com.sks225.accessbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sks225.accessbuddy.MainActivity.Companion.myPager
import com.sks225.accessbuddy.MainActivity.Companion.tabsBtn
import com.sks225.accessbuddy.adapter.TabAdapter
import com.sks225.accessbuddy.databinding.ActivityMainBinding
import com.sks225.accessbuddy.databinding.BookmarkDialogBinding
import com.sks225.accessbuddy.databinding.MoreFeaturesBinding
import com.sks225.accessbuddy.databinding.TabsViewBinding
import com.sks225.accessbuddy.model.Bookmark
import com.sks225.accessbuddy.model.Tab
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var printJob: PrintJob? = null

    companion object {
        var tabsList: ArrayList<Tab> = ArrayList()
        private var isFullscreen: Boolean = false
        var isDesktopSite: Boolean = false
        var bookmarkList: ArrayList<Bookmark> = ArrayList()
        var bookmarkIndex: Int = -1
        lateinit var myPager: ViewPager2
        lateinit var tabsBtn: MaterialTextView
        //lateinit var bottomMenu: BottomMenuFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            window.attributes.layoutInDisplayCutoutMode =
//                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //bottomMenu = BottomMenuFragment()

        lifecycleScope.launch {
            delay(1000)
            (application as AccessBuddyApplication).container.textToSpeechHandler.speak("Hello")
        }

        getAllBookmarks()

        tabsList.add(Tab("Home", HomeFragment()))
        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        binding.myPager.isUserInputEnabled = false
        myPager = binding.myPager
        tabsBtn = binding.tabsBtn

        initializeView()
        //changeFullscreen(enable = true)
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("Restart", "onRestart")
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {
        var frag: BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
        } catch (_: Exception) {
        }

        when {
            frag?.binding?.webView?.canGoBack() == true -> frag.binding.webView.goBack()
            binding.myPager.currentItem != 0 -> {
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1

            }

            else -> super.onBackPressed()
        }
    }


    private inner class TabsAdapter(fa: FragmentManager, lc: Lifecycle) :
        FragmentStateAdapter(fa, lc) {
        override fun getItemCount(): Int = tabsList.size

        override fun createFragment(position: Int): Fragment = tabsList[position].fragment
    }

    private fun initializeView() {
        binding.tabsBtn.setOnClickListener {
            val viewTabs = layoutInflater.inflate(R.layout.tabs_view, binding.root, false)
            val bindingTabs = TabsViewBinding.bind(viewTabs)

            val dialogTabs =
                MaterialAlertDialogBuilder(this, R.style.roundCornerDialog).setView(viewTabs)
                    .setTitle("Select com.sks225.accessbuddy.model.Tab")
                    .setPositiveButton("Home") { self, _ ->
                        changeTab("Home", HomeFragment())
                        self.dismiss()
                    }
                    .setNeutralButton("Google") { self, _ ->
                        changeTab("Google", BrowseFragment(urlNew = "www.google.com"))
                        self.dismiss()
                    }
                    .create()

            bindingTabs.tabsRV.setHasFixedSize(true)
            bindingTabs.tabsRV.layoutManager = LinearLayoutManager(this)
            bindingTabs.tabsRV.adapter = TabAdapter(this, dialogTabs)

            dialogTabs.show()

            val pBtn = dialogTabs.getButton(AlertDialog.BUTTON_POSITIVE)
            val nBtn = dialogTabs.getButton(AlertDialog.BUTTON_NEUTRAL)

            pBtn.isAllCaps = false
            nBtn.isAllCaps = false

            pBtn.setTextColor(Color.BLACK)
            nBtn.setTextColor(Color.BLACK)

            pBtn.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.baseline_home_24, theme),
                null,
                null,
                null
            )
            nBtn.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.baseline_add_24, theme),
                null,
                null,
                null
            )
        }

        binding.btnMenu.setOnClickListener {

            var frag: BrowseFragment? = null
            try {
                frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
            } catch (_: Exception) {
            }

            val view = layoutInflater.inflate(R.layout.more_features, binding.root, false)
            val dialogBinding = MoreFeaturesBinding.bind(view)

            val dialog = MaterialAlertDialogBuilder(this).setView(view).create()

            dialog.window?.let { window ->
                val params = window.attributes
                params.width = WindowManager.LayoutParams.MATCH_PARENT // set width to maximum
                window.attributes = params
            }

            dialog.window?.apply {
                attributes.gravity = Gravity.BOTTOM
                attributes.y = 0
                setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
            }

            dialog.window?.setWindowAnimations(android.R.style.Animation_Translucent)
            dialog.window?.setWindowAnimations(R.style.DialogSwipeUpAnimation)// set your swipe-up animation style here


            dialog.show()

            if (isFullscreen) {
                dialogBinding.fullscreenBtn.apply {
                    setIconTintResource(R.color.black)//cool bue
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.cool_blue))
                }
            }

            frag?.let {
                bookmarkIndex = isBookmarked(it.binding.webView.url!!)
                if (bookmarkIndex != -1) {

                    dialogBinding.bookmarkBtn.apply {
                        setIconTintResource(R.color.cool_blue)
                        setTextColor(ContextCompat.getColor(this@MainActivity, R.color.cool_blue))
                    }
                }
            }

            if (isDesktopSite) {
                dialogBinding.desktopBtn.apply {
                    setIconTintResource(R.color.cool_blue)
                    setTextColor(ContextCompat.getColor(this@MainActivity, R.color.cool_blue))
                }
            }



            dialogBinding.backBtn.setOnClickListener {
                onBackPressed()
            }

            dialogBinding.forwardBtn.setOnClickListener {
                frag?.apply {
                    if (binding.webView.canGoForward())
                        binding.webView.goForward()
                }
            }

            dialogBinding.saveBtn.setOnClickListener {
                dialog.dismiss()
                if (frag != null)
                    saveAsPdf(web = frag!!.binding.webView)
                else Snackbar.make(binding.root, "First Open A WebPage\uD83D\uDE03", 3000).show()
            }

            dialogBinding.fullscreenBtn.setOnClickListener {
                it as MaterialButton

                isFullscreen = if (isFullscreen) {
                    changeFullscreen(enable = false)
                    it.setIconTintResource(R.color.black)
                    it.setTextColor(ContextCompat.getColor(this, R.color.black))
                    false
                } else {
                    changeFullscreen(enable = true)
                    it.setIconTintResource(R.color.cool_blue)
                    it.setTextColor(ContextCompat.getColor(this, R.color.cool_blue))
                    true
                }
            }

            dialogBinding.desktopBtn.setOnClickListener {
                it as MaterialButton

                frag?.binding?.webView?.apply {
                    isDesktopSite = if (isDesktopSite) {
                        settings.userAgentString = null
                        it.setIconTintResource(R.color.black)
                        it.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
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
                        it.setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.cool_blue
                            )
                        )
                        true
                    }
                    reload()
                    dialog.dismiss()
                }

            }

            dialogBinding.bookmarkBtn.setOnClickListener {
                frag?.let {
                    if (bookmarkIndex == -1) {
                        val viewB =
                            layoutInflater.inflate(R.layout.bookmark_dialog, binding.root, false)
                        val bBinding = BookmarkDialogBinding.bind(viewB)
                        val dialogB = MaterialAlertDialogBuilder(this)
                            .setTitle("Add com.sks225.accessbuddy.model.Bookmark")
                            .setMessage("Url:${it.binding.webView.url}")
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
                        val dialogB = MaterialAlertDialogBuilder(this)
                            .setTitle("Remove com.sks225.accessbuddy.model.Bookmark")
                            .setMessage("Url:${it.binding.webView.url}")
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

//        binding.btnMenu.setOnClickListener {
//            if (bottomMenu.isAdded) {
//                bottomMenu.dismiss()
//            } else {
//                bottomMenu.show(supportFragmentManager, bottomMenu.tag)
//            }
//        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        var frag: BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
        } catch (_: Exception) {
        }

        binding.btnForward.setOnClickListener {
            frag?.apply {
                if (binding.webView.canGoForward())
                    binding.webView.goForward()
            }
        }

        binding.btnNewTab.setOnClickListener {
            changeTab("Google", BrowseFragment(urlNew = "https://www.google.com"))
        }

    }

    override fun onResume() {
        super.onResume()
        printJob?.let {
            when {
                it.isCompleted -> Snackbar.make(
                    binding.root,
                    "Successful -> ${it.info.label}",
                    4000
                ).show()

                it.isFailed -> Snackbar.make(binding.root, "Failed -> ${it.info.label}", 4000)
                    .show()
            }
        }
    }

    fun saveAsPdf(web: WebView) {
        val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "${URL(web.url).host}_${
            SimpleDateFormat("HH:mm d_MMM_yy", Locale.ENGLISH)
                .format(Calendar.getInstance().time)
        }"
        val printAdapter = web.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder()
        printJob = pm.print(jobName, printAdapter, printAttributes.build())
    }

    private fun changeFullscreen(enable: Boolean) {
        if (enable) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).show(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun isBookmarked(url: String): Int {
        bookmarkList.forEachIndexed { index, bookmark ->
            if (bookmark.url == url) return index
        }
        return -1
    }

    fun saveBookmarks() {
        //for storing bookmarks data using shared preferences
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE).edit()

        val data = GsonBuilder().create().toJson(bookmarkList)
        editor.putString("bookmarkList", data)

        editor.apply()
    }

    private fun getAllBookmarks() {
        //for getting bookmarks data using shared preferences from storage
        bookmarkList = ArrayList()
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE)
        val data = editor.getString("bookmarkList", null)

        if (data != null) {
            val list: ArrayList<Bookmark> = GsonBuilder().create()
                .fromJson(data, object : TypeToken<ArrayList<Bookmark>>() {}.type)
            bookmarkList.addAll(list)
        } else {
            // add default bookmarks
            bookmarkList.add(
                Bookmark(
                    "Google",
                    "https://www.google.com",
                    null,
                    R.drawable.ic_d_google
                )
            )
            bookmarkList.add(
                Bookmark(
                    "Youtube",
                    "https://youtube.com",
                    null,
                    R.drawable.ic_d_youtube
                )
            )
        }
    }

    fun toggleFullscreen() {
        isFullscreen = if (isFullscreen) {
            changeFullscreen(false)
            false
        } else {
            changeFullscreen(true)
            true
        }
    }

    fun toggleDesktopMode() {
        val frag = getCurrentBrowseFragment()
        frag?.binding?.webView?.apply {
            isDesktopSite = !isDesktopSite
            settings.userAgentString = if (isDesktopSite) {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:99.0) Gecko/20100101 Firefox/99.0"
            } else {
                null
            }
            reload()
        }
    }

//    fun toggleBookmark() {
//        val frag = getCurrentBrowseFragment()
//        frag?.let {
//            bookmarkIndex = isBookmarked(it.binding.webView.url!!)
//            if (bookmarkIndex == -1) {
//                addBookmark(it.binding.webView.url!!)
//            } else {
//                removeBookmark(bookmarkIndex)
//            }
//        }
//    }

    fun goBackInWebView() {
        val frag = getCurrentBrowseFragment()
        frag?.binding?.webView?.takeIf { it.canGoBack() }?.goBack()
    }

    fun goForwardInWebView() {
        val frag = getCurrentBrowseFragment()
        frag?.binding?.webView?.takeIf { it.canGoForward() }?.goForward()
    }

    fun saveCurrentPageAsPdf() {
        val frag = getCurrentBrowseFragment()
        frag?.let {
            saveAsPdf(it.binding.webView)
        }
    }

    private fun getCurrentBrowseFragment(): BrowseFragment? {
        return tabsList.getOrNull(binding.myPager.currentItem)?.fragment as? BrowseFragment
    }

}


@SuppressLint("NotifyDataSetChanged")
fun changeTab(url: String, fragment: Fragment, isBackground: Boolean = false) {
    MainActivity.tabsList.add(Tab(name = url, fragment = fragment))
    myPager.adapter?.notifyDataSetChanged()
    tabsBtn.text = MainActivity.tabsList.size.toString()

    if (!isBackground) myPager.currentItem = MainActivity.tabsList.size - 1
}

fun checkForInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}