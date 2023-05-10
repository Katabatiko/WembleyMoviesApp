package com.gonpas.wembleymoviesapp.ui.tabs.popular

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.gonpas.wembleymoviesapp.MainActivity
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.WembleyMoviesApp
import com.gonpas.wembleymoviesapp.databinding.FragmentPopularMoviesBinding
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModel
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModelFactory
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.MovieListener
import com.gonpas.wembleymoviesapp.ui.dialogs.OverviewDialogFragment
import com.gonpas.wembleymoviesapp.ui.tabs.ApiStatus
import com.gonpas.wembleymoviesapp.utils.MovieType
import com.gonpas.wembleymoviesapp.utils.localNumberFormat


private const val TAG = "xxPmf"

class PopularMoviesFragment : Fragment() {
//    lateinit var pagerAdapter: ViewPagerAdapter

    var buscando = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val app = requireNotNull(activity).application
        val moviesRepository = (requireContext().applicationContext as WembleyMoviesApp).moviesRepository
        // cuando se usa una Factory personalizada, se invoca con 'activityViewModels', si no se usa 'viewModels'
        val viewModel by activityViewModels<MoviesViewModel> { MoviesViewModelFactory(app, moviesRepository, requireActivity()) }

        var searchQuery = ""

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentPopularMoviesBinding>(inflater,R.layout.fragment_popular_movies, container, false)

        binding.viewModel = viewModel
        Log.d(TAG,"savedState: ${viewModel.state.keys()}")

        binding.lifecycleOwner = viewLifecycleOwner


        val recyclerView = binding.listPopularMovies
        val adapter = MoviesAdapter(
            viewModel,
            // filmListener
            MovieListener {
//                Log.d(TAG,it.title)
                viewModel.getCredits(it.id, it.title, it.overview)
            },
            // fabListener
            MovieListener {
                it.fav = true
                viewModel.saveFavMovie(it)
                Toast.makeText(context, "${ getString(R.string.guardada) }: '${it.title}'", Toast.LENGTH_LONG).show()
            },
            // overviewListener
            MovieListener {
                val dialogo = OverviewDialogFragment(it.title, it.overview)
                dialogo.show(parentFragmentManager,getString(R.string.app_name))
            },
            MovieType.POPULAR
        )

        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//                SCROLL_STATE_IDLE = 0 -> reposo
//                SCROLL_STATE_DRAGGING = 1 -> The RecyclerView is currently being dragged by outside input such as user touch input
//                SCROLL_STATE_SETTLING = 2 -> The RecyclerView is currently animating to a final position while not under outside control.
//                                          El RecyclerView actualmente está animando a una posición final mientras no está bajo control externo.
//                Log.d(TAG, "scroll state change: $newState")
                if(!recyclerView.canScrollVertically(1) && newState == 1){
                    if (buscando)       viewModel.searchMovies(searchQuery)
                    else                viewModel.downloadMovies()
                }
            }
        })

        viewModel.status.observe(viewLifecycleOwner){
            if (it == ApiStatus.ERROR){
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.resetStatus()
                }, 5000)
            }
        }

//        viewModel.popularMoviesList.observe(viewLifecycleOwner){list ->
        viewModel.getPops().observe(viewLifecycleOwner){ list ->
//            Log.d(TAG,"pop state: ${viewModel.state.keys()}")
            if (list.isNotEmpty()) {
//                Log.d(TAG,"cambios en popsMovies")
                if (viewModel.favsMovies.value?.isNotEmpty() == true) {
//                if (viewModel.getFavs().value?.isNotEmpty() == true){
                    viewModel.evalFavsInPops(true)
                }
                adapter.submitList(list)
                val totalFormated = localNumberFormat(viewModel.totalMovies)
                binding.totalFilms.text = getText(R.string.total_movies)
                    .toString().format(totalFormated, "")
            }
        }

//        viewModel.getFavs().observe(viewLifecycleOwner){
        viewModel.favsMovies.observe(viewLifecycleOwner){
            Log.d(TAG,"cambios en favsMovies")
            if (viewModel.getPops().value?.isNotEmpty() == true){
                viewModel.evalFavsInPops(false)
            }
        }

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchQuery = p0.toString()
                viewModel.searchMovies(searchQuery)
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

        viewModel.getFound().observe(viewLifecycleOwner){
//        viewModel.foundMovies.observe(viewLifecycleOwner){
//            Log.d(TAG, "buscando: $buscando / encontradas: $it")
            /*it?.forEach{
                Log.d(TAG,"found: ${it.title}")
            } ?: Log.d(TAG,"found null")*/
//            Log.d(TAG,"found state: ${viewModel.state.keys()}")
            if (it != null){
                if (buscando && it.isEmpty()) {
                    Toast.makeText(context, getText(R.string.sin_Resultados), Toast.LENGTH_LONG)
                        .show()
                }

                val totalFormated = localNumberFormat(viewModel.found)
                binding.totalFilms.text = getText(R.string.total_movies)
                    .toString().format(totalFormated, getText(R.string.encontradas))
                adapter.submitList(it)
            }
        }

        viewModel.getFilm().observe(viewLifecycleOwner){
//        viewModel.film.observe(viewLifecycleOwner){
//            Log.d(TAG,"film state: ${viewModel.state.keys()}")
            if (it != null) {
//                Log.d(TAG, "cambios en film: ${it.title}")
                val bundle = Bundle().apply {
                    putParcelable("film", it)
                }
                MainActivity.addOrReplaceMovieTab(bundle)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if (buscando){
                        binding.noFound.visibility = View.GONE
                        viewModel.resetSearch()
                        adapter.submitList(viewModel.getPops().value)
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

    companion object{
        private lateinit var INSTANCE: PopularMoviesFragment

        fun getInstance(): PopularMoviesFragment{
            if (!::INSTANCE.isInitialized){
                INSTANCE = PopularMoviesFragment()
            }
            return INSTANCE
        }
    }

}