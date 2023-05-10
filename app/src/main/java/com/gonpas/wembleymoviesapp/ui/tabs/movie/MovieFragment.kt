package com.gonpas.wembleymoviesapp.ui.tabs.movie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.WembleyMoviesApp
import com.gonpas.wembleymoviesapp.databinding.FragmentMovieBinding
import com.gonpas.wembleymoviesapp.domain.DomainFilm
import com.gonpas.wembleymoviesapp.ui.dialogs.PersonDialogFragment
import com.gonpas.wembleymoviesapp.ui.dialogs.PersonDialogFragment.DismissDialogListener
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModelFactory
import com.gonpas.wembleymoviesapp.utils.CastAdapter
import com.gonpas.wembleymoviesapp.utils.PersonListener

private const val TAG = "xxMf"

class MovieFragment : Fragment() {

    lateinit var film: DomainFilm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            film = it!!.getParcelable("film")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val app = requireNotNull(activity).application
        val moviesRepository = (requireContext().applicationContext as WembleyMoviesApp).moviesRepository
        val viewModel by activityViewModels<MoviesViewModel>{ MoviesViewModelFactory(app, moviesRepository, requireActivity()) }

        val binding: FragmentMovieBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie, container, false)

        val adapter = CastAdapter(
            PersonListener {
                viewModel.getPersonData(it)
            }
        )
        binding.casting.adapter = adapter
        adapter.data = film.cast

        binding.film = film

        viewModel.getPerson().observe(viewLifecycleOwner){
//        viewModel.person.observe(viewLifecycleOwner){
//            Log.d(TAG,"personObserver: ${it?.name ?: "nulo"}")
            if (it != null) {
                val personDialog = PersonDialogFragment.newInstance(it, DismissDialogListener{
                    viewModel.resetPerson()
                })
                personDialog.show(requireActivity().supportFragmentManager, it.name)
//                Log.d(TAG,"person state: ${viewModel.state.keys()}")
            }
        }

        return binding.root
    }


    /*companion object {
        private lateinit var INSTANCE: MovieFragment

        fun getInstance(film: DomainFilm): MovieFragment{
            if (!::INSTANCE.isInitialized) {
                INSTANCE = MovieFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("film", film)
                    }
                }
            } else {
                INSTANCE.film = film
            }
            return INSTANCE
        }
    }*/

    fun getTitle(): String{
        return film.title
    }
}