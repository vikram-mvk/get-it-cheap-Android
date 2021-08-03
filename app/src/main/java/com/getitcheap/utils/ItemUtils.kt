package com.getitcheap.utils

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.ArrayAdapter
import com.getitcheap.R
import java.util.*

object ItemUtils {

    @JvmStatic
    val FOR_RENT = "for_rent"
    @JvmStatic
    val FOR_SALE = "for_sale"

    @JvmStatic
    val S3_BASE_URL = "https://get-it-cheap.s3.amazonaws.com/"

    @JvmStatic
    val currencies: MutableMap<String, String> = getAvailableCurrencies()

    @JvmStatic
    var itemTypesRadioButtonToDbString = mapOf(
        R.id.item_type_rent to "for_rent",
        R.id.item_type_sale to "for_sale"
    )

    @JvmStatic
    var itemTypesDbStringToDisplayString = mapOf(
        "for_rent" to "For Rent",
        "for_sale" to "For Sale"
    )

    @JvmStatic
    var rentalBasisDisplayStringToDbString = mapOf(
        "Per hour" to "per_hour",
        "Per day" to "per_day",
        "Per week" to "per_week",
        "Per month" to "per_month"
    )

    @JvmStatic
    var rentalBasisDbStringToDisplayString = mapOf(
        "per_hour" to "Per hour",
        "per_day" to "Per day",
        "per_week" to "Per week" ,
        "per_month" to "Per month"
    )

    private fun getAvailableCurrencies(): MutableMap<String, String> {
        val locales = Locale.getAvailableLocales()

        // We use TreeMap so that the order of the data in the map sorted
        // based on the country name.
        val currencies: MutableMap<String, String> = TreeMap()

        for (locale in locales) {
            try {
                currencies[locale.displayCountry] = Currency.getInstance(locale).symbol
                currencies[locale.country] = Currency.getInstance(locale).symbol
            } catch (e: Exception) {
                // when the locale is not supported
            }
        }
        return currencies
    }


    @JvmStatic
    fun getCurrencyFromAddress(location : String) : String {
        val country : String = location.split(",").last().trim()
        var currency =  currencies.getOrDefault(country, "")
        if (currency.isEmpty()) {
            var countryCode = ""
            if (country.contains(" ")) {
                for (word in country.split(" ")) {
                    countryCode += word[0]
                }
            }else {
               countryCode = country.substring(0, country.lastIndex)
            }

            currency = currencies.getOrDefault(countryCode.toUpperCase(), "")
        }

        return currency
    }

    @JvmStatic
    fun getCityFromAddress(address : String) : String {
        val currentAddress = address.split(",")
        return currentAddress[currentAddress.lastIndex - 2]
    }

    @JvmStatic
    fun getCityStateStringFromAddress() {

    }



    @JvmStatic
    fun getItemTypeDbString(id :Int) : String {
        return itemTypesRadioButtonToDbString[id]!!
    }

    @JvmStatic
    fun getItemTypeDisplayString(dbString :String) : String {
        return itemTypesDbStringToDisplayString[dbString]!!
    }

    @JvmStatic
    fun getRentalBasisDbString(basis :String) : String {
        return rentalBasisDisplayStringToDbString[basis]!!
    }

    @JvmStatic
    fun getRentalBasisDisplayString(dbString :String) : String {
        return rentalBasisDbStringToDisplayString[dbString]!!
    }

    @JvmStatic
    fun getPriceDisplayString(price :String, address : String) = getCurrencyFromAddress(address) + price

    @JvmStatic
    fun isForRent(type:String) = type == FOR_RENT

    @JvmStatic
    fun getCategorySpinnerAdapter(context: Context, isAddNewItem: Boolean): ArrayAdapter<Categories> {
        val categories = ArrayList<Categories>()
        Categories.values().forEach { category -> categories.add(category) }
        if(isAddNewItem) {
            categories.removeAt(0)
        }
        return ArrayAdapter(context, R.layout.getitcheap_spinner, categories)
    }

    @JvmStatic
    fun getRentalBasisSpinnerAdapter(context: Context): ArrayAdapter<String> {
        return  ArrayAdapter(context, R.layout.getitcheap_spinner,
            rentalBasisDisplayStringToDbString.keys.toTypedArray())
    }

    @JvmStatic
    fun getAddressText(text : String) : SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(RelativeSizeSpan(0.7f),0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    enum class Categories {
        All,
        Electronics,
        Outdoor,
        Clothing,
        Others
    }

    enum class ItemTypes {
        for_rent,
        for_sale
    }

}