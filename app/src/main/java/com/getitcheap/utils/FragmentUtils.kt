package com.getitcheap.utils

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.getitcheap.R

object FragmentUtils {

    @JvmStatic
    val ITEMS_FRAGMENT = "items_fragment"

    @JvmStatic
    val ITEM_DETAILS_FRAGMENT = "item_details_fragment"

    @JvmStatic
    fun getFragmentManager(view : View) : FragmentManager {
        return (view.context as AppCompatActivity).supportFragmentManager
    }

    @JvmStatic
    fun switchFragment(view : View, container: Int, fragment: Fragment) {
        return (view.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(
                container, fragment)
            .commitNow()
    }


    @JvmStatic
    fun switchToPreviousFragment(view: View) : Boolean {
        return (view.context as AppCompatActivity).supportFragmentManager.popBackStackImmediate()

    }


}