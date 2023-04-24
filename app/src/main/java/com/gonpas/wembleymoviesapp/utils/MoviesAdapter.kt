package com.gonpas.wembleymoviesapp.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gonpas.wembleymoviesapp.databinding.FavItemBinding
import com.gonpas.wembleymoviesapp.databinding.MovieItemBinding
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModel

private const val TAG = "xxMAdapter"

class MoviesAdapter(
    private val viewModel: MoviesViewModel,
    private val fabClickListener: FabListener,
    private val overviewListener: OverviewListener,
    private val movieType: MovieType
): ListAdapter<DomainMovie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback){

    override fun getItemViewType(position: Int): Int {
        return movieType.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        when(movieType){
            MovieType.FAVORITE -> {
                val holderBinding = holder.binding as FavItemBinding
                holderBinding.movie = item
                holderBinding.viewModel = viewModel

                holderBinding.overview.setOnClickListener{
                    overviewListener.onClick(item)
                }

                holderBinding.floatingActionButton.setOnClickListener{
                    fabClickListener.onClick(item)
                }

                holderBinding.executePendingBindings()
            }
            else -> {
                val holderBinding = holder.binding as MovieItemBinding
                holderBinding.movie = item
                holderBinding.viewModel = viewModel

                holderBinding.overview.setOnClickListener{
                    overviewListener.onClick(item)
                }

                holderBinding.floatingActionButton.setOnClickListener{
                    fabClickListener.onClick(item)
                    it.visibility = View.GONE
                }
                holderBinding.executePendingBindings()
            }
        }

    }


    class MovieViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun from(parent: ViewGroup, viewType: Int): MovieViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding :ViewDataBinding = when(viewType) {
                    2 -> FavItemBinding.inflate(layoutInflater, parent, false)
                    else -> MovieItemBinding.inflate(layoutInflater, parent, false)
                }
                return MovieViewHolder(binding)
            }
        }
    }

    companion object MovieDiffCallback : DiffUtil.ItemCallback<DomainMovie>(){
        override fun areItemsTheSame(oldItem: DomainMovie, newItem: DomainMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DomainMovie, newItem: DomainMovie): Boolean {
            return oldItem == newItem
        }

    }
}

enum class MovieType(val value: Int){
    POPULAR(1), FAVORITE(2)
}