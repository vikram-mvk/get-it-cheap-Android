package com.getitcheap.utils

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getitcheap.R
import com.google.android.material.snackbar.Snackbar


class Utils {

    companion object {
        @JvmStatic
        fun showSnackBarForFailure(view : View, text: String?) {
            if (text == null || view.context == null) return
            // Close the keyboard, to make the snackbar visible
            val imm: InputMethodManager = (view.context as AppCompatActivity)
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            try {
                val parentView : View = (view.context as AppCompatActivity).findViewById(android.R.id.content)
                val color = view.context.getColor(R.color.errorRedDark)

                Snackbar.make(parentView, text, 2000).setBackgroundTint(color)
                    .setTextColor(Color.WHITE).show()

            }
            catch(e :Exception) {
                    Log.e("Exception in Snackbar", e.toString())
            }
        }

        @JvmStatic
        fun showToastForFailure(view : View, text: String?) {
            if (text == null || view.context == null) return
            // Close the keyboard, to make the snackbar visible
            val imm: InputMethodManager = (view.context as AppCompatActivity)
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            try {
                Toast.makeText(view.context, text, Toast.LENGTH_SHORT).show()
            }
            catch(e :Exception) {
                Log.e("Exception in Toast", e.toString())
            }
        }

        @JvmStatic
        fun showSnackBarForSuccess(view : View, text: String?) {
            if (text == null || view.context == null) return
            val parentView : View = (view.context as AppCompatActivity).findViewById(android.R.id.content)

            try {
                val color = view.context.getColor(R.color.successGreen)
                Snackbar.make(parentView, text, 2000).setBackgroundTint(color)
                    .setTextColor(Color.WHITE).show()
            }
            catch(e :Exception) {
                Log.e("Exception in Snackbar", e.toString())
            }
        }

    }

}