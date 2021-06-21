package com.getitcheap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity : AppCompatActivity() {
    lateinit var navBar : BottomNavigationView
    var sButtonFragmentMap = mapOf<Int, Fragment>(
        R.id.navbar_items to ItemsFragment(),
        R.id.navbar_favourites to ItemsFragment(),
        R.id.navbar_account to AccountFragment()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        navBar = findViewById(R.id.nav_bar)
        navBar.setOnNavigationItemSelectedListener()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
    }

    private fun switchBaseFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.base_fragment_container, fragment)
            .commitNow()
    }

}