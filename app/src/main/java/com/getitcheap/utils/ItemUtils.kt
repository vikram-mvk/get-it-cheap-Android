package com.getitcheap.utils

import com.getitcheap.R

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

    enum class Categories {
        electronics,
        outdoor,
        clothing,
        others
    }

    enum class ItemTypes {
        for_rent,
        for_sale
    }

}