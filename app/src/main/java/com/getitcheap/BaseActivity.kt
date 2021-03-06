package com.getitcheap

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.getitcheap.data.SharedPrefs
import com.getitcheap.user.AccountFragment
import com.getitcheap.item.AddNewItemFragment
import com.getitcheap.item.ItemsFragment
import com.getitcheap.item.ShowAddButton
import com.getitcheap.web_api.RetroFitService.userApi
import com.getitcheap.web_api.response.MessageResponse
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseActivity : AppCompatActivity() {
    lateinit var sharedPrefsInstance : SharedPrefs
    lateinit var placesClient : PlacesClient

    private val showAddButtonImpl = object: ShowAddButton {
        override fun showAddButtonInMenu(shown: Boolean) {
            setAddButtonShown(shown)
        }
    }

    private var sButtonFragmentMap = mapOf<Int, Fragment>(
        R.id.navbar_items to ItemsFragment.newInstance(),
        R.id.navbar_new_item to AddNewItemFragment.newInstance(),
        R.id.navbar_account to AccountFragment.newInstance()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        /*
        userApi.isServerRunning().enqueue(object: Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) { }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                MaterialAlertDialogBuilder(this@BaseActivity)
                    .setTitle("Server Maintenance")
                    .setMessage("Sorry!\nThe app is currently not usable due to server Maintenance.\nPlease check again later")
                    .setNeutralButton("OK", object: DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            this@BaseActivity.finishAffinity()
                        }

                    })
                    .show()
                    .setOnDismissListener {
                        this@BaseActivity.finishAffinity()
                    }
            }
        })
        */

        sharedPrefsInstance = SharedPrefs.getInstance(this@BaseActivity)
        navBar = findViewById(R.id.nav_bar)
        navBar.setOnNavigationItemSelectedListener {
            sButtonFragmentMap[it.itemId]?.let { f -> switchBaseFragment(this@BaseActivity, f) }
            return@setOnNavigationItemSelectedListener true
        }
        (sButtonFragmentMap[R.id.navbar_account] as AccountFragment).showOrHideAddItem(showAddButtonImpl)
        setAddButtonShown(sharedPrefsInstance.getEmail().isNotEmpty())


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), 1340);
        } else {
            navBar.selectedItemId = R.id.navbar_items
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        navBar.selectedItemId = R.id.navbar_items
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    fun setAddButtonShown(shown : Boolean) {
        navBar.menu.findItem(R.id.navbar_new_item).isVisible = shown
        navBar.invalidate()
    }

    companion object {
        @JvmStatic
        lateinit var navBar: BottomNavigationView

        @JvmStatic
        fun switchPage(context : Context, id : Int) {
            navBar.selectedItemId = id
        }

        @JvmStatic
        fun switchBaseFragment(context: Context, fragment: Fragment) {

        val activity = (context as BaseActivity)
            activity.supportActionBar?.subtitle = ""
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.base_fragment_container, fragment)
                .commit()
            Handler().postDelayed( {activity.supportActionBar?.title = navBar.menu.findItem(navBar.selectedItemId).title
                .toString().toLowerCase().capitalize()} , 50)
        }

    }
}