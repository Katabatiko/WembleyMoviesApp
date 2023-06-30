package com.gonpas.wembleymoviesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.gonpas.wembleymoviesapp.databinding.ActivityMainBinding
import com.gonpas.wembleymoviesapp.utils.DiffPagerAdapter
import com.gonpas.wembleymoviesapp.utils.DiffPagerAdapter.PagerFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@VisibleForTesting
lateinit var pager: ViewPager2

private const val TAG = "xxMa"

lateinit var tab: TabLayout
private lateinit var adapter: DiffPagerAdapter
private var selectedItem = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    /*private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                .EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }*/

//    private lateinit var imgNoInternet: ImageView
//    private lateinit var msgNoInternet: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null){
            selectedItem = savedInstanceState.getInt("selectedItem")
        }

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        pager = binding.viewPager
//        pager.setPageTransformer(TranslationPageTransformer())
//        pager.isUserInputEnabled = false
        tab = binding.tabs


        adapter = DiffPagerAdapter(supportFragmentManager, lifecycle)
        adapter.setItems(generateInitialPagerFragments())

        pager.adapter = adapter

        TabLayoutMediator(tab, pager){ tab, position ->
            when(position){
                0 -> {
                    tab.text = resources.getString(R.string.first_fragment_label)
                    tab.setIcon(R.drawable.ic_popular)
                }
                1 -> {
                    tab.text = resources.getString(R.string.second_fragment_label)
                    tab.setIcon(R.drawable.ic_favorite)
                }
                else -> {
                    tab.text = "Película"
                    tab.setIcon(R.drawable.ic_movie)
                }
            }
        }.attach()

//        imgNoInternet = binding.noInternetImg
//        msgNoInternet = binding.noInternetMsg
    }

    /*override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }*/



    override fun onPause() {
        super.onPause()
        selectedItem = pager.currentItem
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedItem", selectedItem)
    }

    private fun generateInitialPagerFragments(): MutableList<PagerFragment>{
        return mutableListOf(
            PagerFragment(0, "f0"),
            PagerFragment(1,"f1")
        )
    }

//    private fun disconnected() {
////        recyclerView.visibility = View.INVISIBLE
//        // favoritos no requiere internet
////        if (pager.currentItem != 1) {
//            imgNoInternet.visibility = View.VISIBLE
//            msgNoInternet.visibility = View.VISIBLE
////        }
//    }

//    private fun connected() {
////        recyclerView.visibility = View.VISIBLE
//        imgNoInternet.visibility = View.GONE
//        msgNoInternet.visibility = View.GONE
//    }



    companion object {
        fun addOrReplaceMovieTab(filmTitle: String){

            selectedItem = 2

            val newFrags = listOf(
                PagerFragment(0, "f0"),
                PagerFragment(1,"f1"),
                PagerFragment(2,"f2", filmTitle)
            )
            adapter.setItems(newFrags)

            pager.currentItem = selectedItem
            tab.selectTab(tab.getTabAt(2), true)


            val tab0Text = tab.getTabAt(0)?.text
            val tab1Text = tab.getTabAt(1)?.text

            TabLayoutMediator(tab, pager) { tab, position ->
                when(position){
                    0 -> {
                        tab.text = tab0Text
                        tab.setIcon(R.drawable.ic_popular)
                    }
                    1 -> {
                        tab.text = tab1Text
                        tab.setIcon(R.drawable.ic_favorite)
                    }
                    else -> {
                        tab.text = filmTitle
                        tab.setIcon(R.drawable.ic_movie)
                    }
                }
            }.attach()

        }
    }
}

class TranslationPageTransformer : ViewPager2.PageTransformer {
    /** The default animation (a screen slide) still takes place,
     * so you must counteract the screen slide with a negative X translation. For example:
     * view.translationX = -1 * view.width * position
     */
    override fun transformPage(page: View, position: Float) {
        Log.d(TAG,"page: ${page.id} / position: $position")
        page.apply{
            val pageWidth = width
            when{
                position < 0 -> {// [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position == 0f -> {// [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1 - position
                    translationX = pageWidth * -position
                    translationY = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> {// [0,1]
                    // Fade the page out.
                    alpha = 1 - position
                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationY = -1f
                }
                else -> {// [1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}