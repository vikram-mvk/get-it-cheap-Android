package com.getitcheap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BaseActivity : AppCompatActivity() {
    lateinit var navBar : BottomNavigationView
    var sButtonFragmentMap = mapOf<Int, Fragment>(
        R.id.navbar_items to ItemsFragment(),
        R.id.navbar_favourites to ItemsFragment(),
        R.id.navbar_new_item to AddNewItemFragment(),
        R.id.navbar_account to AccountFragment()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        navBar = findViewById(R.id.nav_bar)
        navBar.setOnNavigationItemSelectedListener {
            sButtonFragmentMap[it.itemId]?.let { f -> switchBaseFragment(f) }
            return@setOnNavigationItemSelectedListener true
        }
        navBar.selectedItemId = R.id.navbar_items
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

    companion object {
        @JvmStatic
        var token:String = "Bearer %s"
    }
}