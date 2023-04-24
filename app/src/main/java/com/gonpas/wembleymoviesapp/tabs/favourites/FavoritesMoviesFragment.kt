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
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.WembleyMoviesApp
import com.gonpas.wembleymoviesapp.databinding.FragmentFavoritesMoviesBinding
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModelFactory
import com.gonpas.wembleymoviesapp.utils.*

//private const val TAG = "xxFmf"

class FavoritesMoviesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val app = requireNotNull(activity).application
//        val database = getDatabase(app)
//        val moviesRepository = MoviesRepository(TmdbApi.tmdbApiService, database.movieDao)
        val moviesRepository = (requireContext().applicationContext as WembleyMoviesApp).moviesRepository
        val viewModelFactory = MoviesViewModelFactory(app, moviesRepository)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MoviesViewModel::class.java]

        val binding = DataBindingUtil.inflate<FragmentFavoritesMoviesBinding>(inflater, R.layout.fragment_favorites_movies, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = MoviesAdapter(
            viewModel,
            FabListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(getString(R.string.eliminar).format(it.title))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.confirmar)){ _, _ ->
                        it.fav = false
                        viewModel.removeMovie(it.id)
                    }
                    .setNegativeButton(android.R.string.cancel){ dialog, _ ->
                        dialog.dismiss()
                    }
                builder.create().show()
            },
            OverviewListener {
                val dialogo = OverviewDialogFragment(it.title, it.overview)
                dialogo.show(parentFragmentManager,getString(R.string.app_name))
            },
            MovieType.FAVORITE
        )
        binding.listFavMovies.adapter = adapter

        viewModel.favsMovies.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        return binding.root
    }

}