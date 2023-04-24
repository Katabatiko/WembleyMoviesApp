package com.gonpas.wembleymoviesapp.tabs.popular

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.WembleyMoviesApp
import com.gonpas.wembleymoviesapp.databinding.FragmentPopularMoviesBinding
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.tabs.MoviesViewModelFactory
import com.gonpas.wembleymoviesapp.utils.*


private const val TAG = "xxPmf"

class PopularMoviesFragment : Fragment() {

    var buscando = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app = requireNotNull(activity).application
//        val database = getDatabase(app)
//        val moviesRepository = MoviesRepository(TmdbApi.tmdbApiService, database.movieDao)
        val moviesRepository = (requireContext().applicationContext as WembleyMoviesApp).moviesRepository
        val viewModelFactory = MoviesViewModelFactory(app, moviesRepository)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MoviesViewModel::class.java]

        var searchQuery = ""

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentPopularMoviesBinding>(inflater,R.layout.fragment_popular_movies, container, false)

        binding.viewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        val recyclerView = binding.listPopularMovies
        val adapter = MoviesAdapter(
            viewModel,
            FabListener {
                it.fav = true
                viewModel.saveFavMovie(it)
                Toast.makeText(context, getString(R.string.guardada), Toast.LENGTH_LONG).show()
            },
            OverviewListener {
                val dialogo = OverviewDialogFragment(it.title, it.overview)
                dialogo.show(parentFragmentManager,getString(R.string.app_name))
            },
            MovieType.POPULAR
        )

        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            /*override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!recyclerView.canScrollVertically(1) && newState != 0){
                    if (buscando)       viewModel.searchMovies(searchQuery)
                    else                viewModel.downloadMovies()
                }
            }*/
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                Log.d(TAG,"onScrolled")
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)){
                    if (buscando)       viewModel.searchMovies(searchQuery)
                    else                viewModel.downloadMovies()
                }
            }
        })

        viewModel.popularMoviesList.observe(viewLifecycleOwner){list ->
            if (list.isNotEmpty()) {
//                Log.d(TAG,"cambios en popsMovies")
                if (viewModel.favsMovies.value?.isNotEmpty() == true) {
                    viewModel.evalFavsInPops(true)
                }
                adapter.submitList(list)
                val totalFormated = localNumberFormat(viewModel.totalMovies)
                binding.totalFilms.text = getText(R.string.total_movies)
                    .toString().format(totalFormated, "")
            }
        }

        viewModel.favsMovies.observe(viewLifecycleOwner){
//            Log.d(TAG,"cambios en favsMovies")
            if (viewModel.popularMoviesList.value?.isNotEmpty() == true){
                viewModel.evalFavsInPops(false)
            }
        }

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchQuery = p0.toString()
                viewModel.searchMovies(searchQuery)
                viewModel.resetSearchControls()
                hideKeyboard()
                buscando = true
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                /*if (!p0.isNullOrBlank() && p0.length > 2){
                    viewModel.searchMovies(p0)
                    buscando = true
                }*/
                return true
            }
        })

        viewModel.foundMovies.observe(viewLifecycleOwner){
//            Log.d(TAG, "buscando: $buscando / encontradas: $it")
            if (buscando && it.isEmpty()) {
                Toast.makeText(context, getText(R.string.sin_Resultados), Toast.LENGTH_LONG).show()
            }

            val totalFormated = localNumberFormat(viewModel.found)
            binding.totalFilms.text = getText(R.string.total_movies)
                                        .toString().format(totalFormated, getText(R.string.encontradas))
            adapter.submitList(it)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if (buscando){
                        binding.noFound.visibility = View.GONE
                        adapter.submitList(viewModel.popularMoviesList.value)
                        val totalFormated = localNumberFormat(viewModel.totalMovies)
                        binding.totalFilms.text = getText(R.string.total_movies)
                            .toString().format(totalFormated, "")
                        buscando = false
                        searchQuery = ""
                    } else {
                        this.remove()
                        OnBackPressedDispatcher()
                    }
                }
            }
        )

        return binding.root
    }


    private fun Context.hideKeyboard(view: View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard(){
        view?.let { activity?.hideKeyboard(it) }
    }

}