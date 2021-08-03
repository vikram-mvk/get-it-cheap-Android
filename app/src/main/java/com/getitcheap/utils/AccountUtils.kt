package com.getitcheap.utils

import com.getitcheap.R

object AccountUtils {

    @JvmStatic
    val IS_NOT_EMPTY = Regex("[\\w\\d]")

    @JvmStatic
    val NAME_REGEX = Regex("^[A-Za-z ]{1,30}$")

    @JvmStatic
    val EMAIL_REGEX = Regex("^[^@]+@[^@]+\\.[^@]+\$")

    @JvmStatic
    val PASSWORD_REGEX = Regex("[\\w\\d!@#$%^&*]{6,15}")

    @JvmStatic
    fun getRegexForField(fieldId : Int) : Regex {
        var regex = IS_NOT_EMPTY
        when(fieldId) {
            R.id.first_name_input -> regex = NAME_REGEX
            R.id.last_name_input -> regex = NAME_REGEX
            R.id.email_input -> regex = EMAIL_REGEX
            R.id.password_input -> regex = PASSWORD_REGEX
        }
        return regex
    }
}