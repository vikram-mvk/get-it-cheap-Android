package com.getitcheap.data

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs {

    val JWT_TOKEN: String = "JWT_TOKEN"
    val USER_NAME: String = "USER_NAME"
    val FIRST_NAME: String = "FIRST_NAME"
    val LAST_NAME: String = "LAST_NAME"
    val EMAIL: String = "EMAIL"
    val USER_ID: String = "USER_ID"
    val GPS_ADDRESS: String = "GPS_ADDRESS"
    val GPS_CITY: String = "GPS_CITY"
    val GPS_STATE: String = "GPS_STATE"
    val GPS_COUNTTY: String = "GPS_COUNTRY"

    val FILTER_CITY: String = "FILTER_CITY"
    val FILTER_STATE: String = "FILTER_STATE"
    val FILTER_COUNTTY: String = "FILTER_COUNTRY"


    private val GET_IT_CHEAP_SHARED_PREFS = "get_it_cheap_shared_prefs"
    private var mSharedPrefs :SharedPreferences


    private constructor(context : Context) {
        mSharedPrefs = context.getSharedPreferences(GET_IT_CHEAP_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    private fun getString(key : String, defaultValue : String) : String {
        return mSharedPrefs.getString(key, defaultValue)!!
    }

    fun putString(key : String, value: String) {
        val editor = mSharedPrefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    private fun getInt(key : String, defaultValue : Int) : Int {
        return mSharedPrefs.getInt(key, defaultValue)
    }

    private fun putInt(key : String, value: Int) {
        val editor = mSharedPrefs.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    private fun getDouble(key : String, defaultValue : Double) : Long {
        return mSharedPrefs.getLong(key, defaultValue.toLong())
    }

    private fun putDouble(key : String, value : Double) {
        val editor = mSharedPrefs.edit()
        editor.putLong(key, value.toLong())
        editor.commit()
    }

    private fun getLong(key : String, defaultValue : Long) : Long {
        return mSharedPrefs.getLong(key, defaultValue)
    }

    private fun putLong(key : String, value : Long) {
        val editor = mSharedPrefs.edit()
        editor.putLong(key, value)
        editor.commit()
    }


    public fun remove(key:String) {
        val editor = mSharedPrefs.edit()
        editor.remove(key)
        editor.commit()
    }

    public fun clearAll() {
        val editor = mSharedPrefs.edit()
        editor.clear()
        editor.commit()
    }

    public fun getJwtToken() : String {
        return getString(JWT_TOKEN, "")
    }

    public fun setJwtToken(token : String) {
        putString(JWT_TOKEN, "Bearer $token")
    }

    public fun getUsername() : String {
        return getString(USER_NAME, "")
    }

    public fun setUsername(username: String) {
        putString(USER_NAME, username)
    }


    public fun getGPSAddress() : String {
        return getString(GPS_ADDRESS, "")
    }

    public fun setGPSAddress(address : String) {
        putString(GPS_ADDRESS, address)
        val addressParts = address.split(",")
        putString(GPS_CITY, (addressParts.getOrElse(1) {""}).trim())
        var state = (addressParts.getOrElse(2) {""}).trim()
        if (state.contains(" ")) {
            state = (state.split(" ").getOrElse(0) {""}).trim()
        }
        putString(GPS_STATE, state)
        putString(GPS_COUNTTY, (addressParts.getOrElse(3) {""}).trim())
    }

    public fun getGPSCity() = getString(GPS_CITY, "Boston")
    public fun getGPSState() = getString(GPS_STATE, "MA")
    public fun getGPSCountry() = getString(GPS_COUNTTY, "USA")

    public fun setFirstName(firstName : String) {
        putString(FIRST_NAME, firstName)
    }

    public fun getFirstName() : String {
       return getString(FIRST_NAME, "")
    }

    public fun setLastName(lastName : String) {
        putString(LAST_NAME, lastName)
    }

    public fun getLastName() : String {
        return getString(LAST_NAME, "")
    }

    public fun getEmail() : String {
        return getString(EMAIL, "")
    }

    public fun setEmail(email: String) {
        putString(EMAIL, email)
    }

    public fun getUserId() : Long {
        return getLong(USER_ID, 0)
    }

    public fun setUserId(userId: Long) {
        putLong(USER_ID, userId)
    }


    companion object {

        @JvmStatic
        @Volatile private var sSharedPrefs : SharedPrefs? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context) : SharedPrefs {
            if (sSharedPrefs == null) {
                sSharedPrefs = SharedPrefs(context)
            }
            return sSharedPrefs!!
        }

    }

}
