package com.gonpas.wembleymoviesapp.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.gonpas.wembleymoviesapp.ui.tabs.movie.MovieFragment
import com.gonpas.wembleymoviesapp.ui.tabs.favourites.FavoritesMoviesFragment
import com.gonpas.wembleymoviesapp.ui.tabs.popular.PopularMoviesFragment

private const val TAG = "xxDiffpa"

class DiffPagerAdapter(
    val fragmentManager: FragmentManager,
    val lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle)  {

    private val items: ArrayList<PagerFragment> = arrayListOf()

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
//            Log.d(TAG,"payloads received: $payloads")

        super.onBindViewHolder(holder, position, payloads)
    }

    override fun createFragment(position: Int): Fragment {
        Log.d(TAG, "Create fragment in diffpageradapter: position $position with ${fragmentManager.fragments.size} items")
        return when(position){
            0 -> {
                PopularMoviesFragment()
            }
            1 -> {
                FavoritesMoviesFragment()
            }
            else -> {
                MovieFragment()
            }
        }
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int): Long {
        return items[position].fragId.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return items.any { it.fragId.toLong() == itemId }
    }


    fun setItems(newItems: List<PagerFragment>) {
        val callback = PagerDiffUtil(items, newItems)
        val diff = DiffUtil.calculateDiff(callback)

        diff.dispatchUpdatesTo(this)

        items.clear()
        items.addAll(newItems)
    }

    data class PagerFragment(
        val fragId: Int,
        val fragTag: String,
        var filmTitle: String = ""
    )
}

class PagerDiffUtil(
    private val oldList: List<DiffPagerAdapter.PagerFragment>,
    private val newList: List<DiffPagerAdapter.PagerFragment>
) : DiffUtil.Callback()  {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].fragId == newList[newItemPosition].fragId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].filmTitle == newList[newItemPosition].filmTitle
    }

    /*override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        Log.d(TAG,"getPayload: ${newList[newItemPosition].filmTitle}")
        return newList[newItemPosition].filmTitle
    }*/
}