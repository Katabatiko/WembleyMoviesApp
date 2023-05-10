package com.gonpas.wembleymoviesapp.ui.tabs.favourites

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
//import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.WembleyMoviesApp
import com.gonpas.wembleymoviesapp.databinding.FragmentFavoritesMoviesBinding
import com.gonpas.wembleymoviesapp.ui.dialogs.OverviewDialogFragment
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModelFactory
import com.gonpas.wembleymoviesapp.utils.*

private const val TAG = "xxFmf"

class FavoritesMoviesFragment : Fragment() {

//    lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app = requireNotNull(activity).application
//        val database = getDatabase(app)
//        val moviesRepository = MoviesRepository(TmdbApi.tmdbApiService, database.movieDao)
        val moviesRepository = (requireContext().applicationContext as WembleyMoviesApp).moviesRepository
//        val viewModelFactory = MoviesViewModelFactory(app, moviesRepository, requireActivity())
        // cuando se usa una Factory personalizada, se invoca con 'activityViewModels', si no se usa 'viewModels'
        val viewModel by activityViewModels<MoviesViewModel>{ MoviesViewModelFactory(app, moviesRepository, requireActivity()) }
//        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MoviesViewModel::class.java]

        val binding = DataBindingUtil.inflate<FragmentFavoritesMoviesBinding>(inflater, R.layout.fragment_favorites_movies, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = MoviesAdapter(
            viewModel,
            // filmListener
            MovieListener {
                viewModel.getCredits(it.id, it.title, it.overview)
            },
            //fabListener
            MovieListener {
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
            // overviewListener
            MovieListener {
                val dialogo = OverviewDialogFragment(it.title, it.overview)
                dialogo.show(parentFragmentManager,getString(R.string.app_name))
            },
            MovieType.FAVORITE
        )
        binding.listFavMovies.adapter = adapter

//        viewModel.getFavs().observe(viewLifecycleOwner){
        viewModel.favsMovies.observe(viewLifecycleOwner){
            Log.d(TAG,"observando getFavs: $it")
            adapter.submitList(it)
        }


        return binding.root
    }

}