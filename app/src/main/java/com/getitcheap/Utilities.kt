package com.getitcheap

import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar

class Utilities {

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