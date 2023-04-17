package com.gonpas.wembleymoviesapp.tabs.favourites

import android.app.AlertDialog
import android.os.Bundle
//import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.database.getDatabase
import com.gonpas.wembleymoviesapp.databinding.FavItemBinding
import com.gonpas.wembleymoviesapp.databinding.FragmentFavoritesMoviesBinding
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.network.TmdbApi
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModelFactory
import com.gonpas.wembleymoviesapp.utils.FabListener
import com.gonpas.wembleymoviesapp.utils.OverviewDialogFragment
import com.gonpas.wembleymoviesapp.utils.OverviewListener

//private const val TAG = "xxFmf"

class FavoritesMoviesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val app = requireNotNull(activity).application
        val database = getDatabase(app)
        val moviesRepository = MoviesRepository(TmdbApi.tmdbApiService, database.movieDao)
        val viewModelFactory = MoviesViewModelFactory(app, moviesRepository)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MoviesViewModel::class.java]

        val binding = DataBindingUtil.inflate<FragmentFavoritesMoviesBinding>(inflater, R.layout.fragment_favorites_movies, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = FavoritesAdapter(
            viewModel,
            FabListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(getString(R.string.eliminar))
                    .setCancelable(false)
                    .setPositiveButton("Confirmar"){ _, _ ->
                        it.fav = false
                        viewModel.removeMovie(it.id)
                    }
                    .setNegativeButton("Cancelar"){ dialog, _ ->
                        dialog.dismiss()
                    }
                builder.create().show()
            },
            OverviewListener {
                val dialogo = OverviewDialogFragment(it.title, it.overview)
                dialogo.show(parentFragmentManager,getString(R.string.app_name))
            }
        )
        binding.listFavMovies.adapter = adapter

//
        viewModel.favsMovies.observe(viewLifecycleOwner){
            adapter.submitList(it)
            /*if (it.isEmpty())   binding.avisoNoHay.visibility = View.VISIBLE
            else                binding.avisoNoHay.visibility = View.GONE*/
        }

        return binding.root
    }

    fun findInPops(favId: Int){
        TODO()
    }


    class FavoritesAdapter(
        val viewModel: MoviesViewModel,
        private val fabClickListener: FabListener,
        private val overviewListener: OverviewListener
    ): ListAdapter<DomainMovie, FavoritesAdapter.FavoriteViewHolder>(MovieDiffCallback){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
            return FavoriteViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
            val item = getItem(position)
            holder.binding.movie = item
            holder.binding.viewModel = viewModel

            holder.binding.overview.setOnClickListener{
                overviewListener.onClick(item)
            }

            holder.binding.floatingActionButton.setOnClickListener{
                fabClickListener.onClick(item)
            }
            holder.binding.executePendingBindings()
        }


        class FavoriteViewHolder(val binding: FavItemBinding): RecyclerView.ViewHolder(binding.root){
            companion object{
                fun from(parent: ViewGroup): FavoriteViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = FavItemBinding.inflate(layoutInflater, parent, false)
                    return FavoriteViewHolder(binding)
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

}