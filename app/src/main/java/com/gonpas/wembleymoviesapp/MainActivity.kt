package com.gonpas.wembleymoviesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.gonpas.wembleymoviesapp.tabs.favourites.FavoritesMoviesFragment
import com.gonpas.wembleymoviesapp.tabs.popular.PopularMoviesFragment
import com.gonpas.wembleymoviesapp.utils.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    private lateinit var pager: ViewPager
    private lateinit var tab: TabLayout
    private lateinit var bar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)
        bar = findViewById(R.id.toolbar)

        setSupportActionBar(bar)

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(PopularMoviesFragment(), resources.getString(R.string.first_fragment_label))
        adapter.addFragment(FavoritesMoviesFragment(), resources.getString(R.string.second_fragment_label))

        pager.adapter = adapter

        tab.setupWithViewPager(pager)


    }
}