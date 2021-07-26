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
    fun getPriceDisplayString(price :String) = String.format("$%s", price)

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
    fun getLocationText(text : String) : SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(RelativeSizeSpan(0.8f),0, Math.min(text.length, 20), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, Math.min(text.length, 20), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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