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
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.databinding.FragmentFavoritesMoviesBinding
import com.gonpas.wembleymoviesapp.ui.dialogs.OverviewDialogFragment
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.utils.*
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "xxFmf"

@AndroidEntryPoint
class FavoritesMoviesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel by activityViewModels<MoviesViewModel>()

        val binding = DataBindingUtil.inflate<FragmentFavoritesMoviesBinding>(inflater, R.layout.fragment_favorites_movies, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = MoviesAdapter(
            viewModel,
            // filmListener
            MovieListener {
                Log.d(TAG,"click en ${it.title}")
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

        viewModel.favsMovies.observe(viewLifecycleOwner) {
            viewModel.renewFavsMovies()
        }

        viewModel.getFavs().observe(viewLifecycleOwner){
//            Log.d(TAG,"observando favs: ${it.size}")
            adapter.submitList(it)
        }

        return binding.root
    }

}