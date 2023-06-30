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
import com.gonpas.wembleymoviesapp.databinding.FragmentMovieBinding
import com.gonpas.wembleymoviesapp.ui.dialogs.PersonDialogFragment
import com.gonpas.wembleymoviesapp.ui.dialogs.PersonDialogFragment.DismissDialogListener
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.utils.CastAdapter
import com.gonpas.wembleymoviesapp.utils.PersonListener
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "xxMf"

@AndroidEntryPoint
class MovieFragment : Fragment() {

    val viewModel by activityViewModels<MoviesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        Log.d(TAG,"fragArgs: ${arguments?.toString() ?: "nulo"}")

        val binding: FragmentMovieBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie, container, false)

        val adapter = CastAdapter(
            PersonListener {
                viewModel.getPersonData(it)
            }
        )
        binding.casting.adapter = adapter

        binding.film = viewModel.getFilm().value
        adapter.data = viewModel.getFilm().value?.cast ?: listOf()


        viewModel.getPerson().observe(viewLifecycleOwner){
//        viewModel.person.observe(viewLifecycleOwner){
            if (it != null) {
                val personDialog = PersonDialogFragment.newInstance(it, DismissDialogListener{
                    viewModel.resetPerson()
                })
                personDialog.show(requireActivity().supportFragmentManager, it.name)
            }
        }

        return binding.root
    }


    companion object {
        private lateinit var INSTANCE: MovieFragment

        fun getInstance(): MovieFragment{
            if (!::INSTANCE.isInitialized)
                INSTANCE = MovieFragment()

            return INSTANCE
        }
    }
}