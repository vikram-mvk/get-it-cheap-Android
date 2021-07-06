package com.getitcheap.utils

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.getitcheap.R
import com.google.android.material.snackbar.Snackbar

class Utils {

    companion object {
        @JvmStatic
        fun showSnackBarForFailure(view : View, text: String) {
            try {
                Snackbar.make(view, text, 2000).setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE).show()
            }
            catch(e :Exception) {
                    Log.e("Exception in Snackbar", e.toString())
            }
        }

        @JvmStatic
        fun showSnackBarForSuccess(view : View, text: String) {
            try {
                Snackbar.make(view, text, 2000).setBackgroundTint(Color.GREEN)
                    .setTextColor(Color.BLACK).show()
            }
            catch(e :Exception) {
                Log.e("Exception in Snackbar", e.toString())
            }
        }

    }

}