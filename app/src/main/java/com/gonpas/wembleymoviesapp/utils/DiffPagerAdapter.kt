package com.gonpas.wembleymoviesapp.utils

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.gonpas.wembleymoviesapp.domain.DomainFilm
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
        if (payloads.isNotEmpty()) {
            when(holder.itemId.toInt()){
                2 -> {
                    val fragment = fragmentManager.findFragmentByTag("f2")
                    // safe check, but fragment should not be null here
                    if (fragment != null) {
                        (fragment as MovieFragment).film = payloads[0] as DomainFilm
                    } else {
                        super.onBindViewHolder(holder, position, payloads)
                    }
                }
                else -> super.onBindViewHolder(holder, position, payloads)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun createFragment(position: Int): Fragment {
//        Log.d(TAG, "Create fragment in diffpageradapter: position $position with ${fragmentManager.fragments.size} items")
        return when(position){
            0 -> {
                PopularMoviesFragment.getInstance()
            }
            1 -> {
                FavoritesMoviesFragment()
            }
            else -> {
                val fragment = MovieFragment().apply {
                    this.arguments = bundleOf("film" to items[2].film)
                }
                return fragment
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

        items.clear()
        items.addAll(newItems)

        diff.dispatchUpdatesTo(this)
    }

    data class PagerFragment(
        val fragId: Int,
        val fragTag: String,
        var film: DomainFilm?
    ): Fragment()
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
        return oldList[oldItemPosition].film == newList[newItemPosition].film
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        return newList[newItemPosition].film!!
    }
}