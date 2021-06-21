package com.getitcheap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment

class BaseActivity : AppCompatActivity() {
    lateinit var navBar : RadioGroup

    var sButtonFragmentMap = hashMapOf<Int, Fragment>(
        R.id.nav_button_for_sale to ItemsFragment(),
        R.id.nav_button_for_rent to ItemsFragment(),
        R.id.nav_button_account to AccountFragment()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        navBar = findViewById(R.id.nav_bar)
        navBar.setOnCheckedChangeListener { group, checkedId ->
            sButtonFragmentMap.get(checkedId)?.let { switchBaseFragment(it) }
        }
        navBar.check(R.id.nav_button_for_rent)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
    }

    fun switchBaseFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.base_fragment_container, fragment)
            .commitNow()
    }

}