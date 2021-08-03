package com.getitcheap.item

import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.getitcheap.R
import com.getitcheap.utils.ItemUtils
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class FilterDialog(itemsFragment: ItemsFragment) {

    private var filterDialog: AlertDialog
    private var filterDialogView : View
    private var rentalBasisFilterLayout : LinearLayout
    private var rentalBasisInput : Spinner
    private var categoryInput : Spinner
    private var addressInput : AutocompleteSupportFragment
    private var applyFiltersButton : MaterialButton
    private var clearFilters : MaterialButton
    private var shouldReloadItems = false
    private var zipCodeInput : TextInputEditText
    private var locationHint = itemsFragment.requireContext().getString(R.string.filter_by_city)
    private var closeButton : ImageButton

    // Actual Filter values
    private var categoriesFilter = HashSet<String>()
    private var citiesFilter = HashSet<String>()
    private var statesFilter = HashSet<String>()
    private var countriesFilter = HashSet<String>()
    private var zipcodesFilter = HashSet<String>()

    init {
        filterDialog = MaterialAlertDialogBuilder(itemsFragment.requireContext()).create()
        filterDialogView = itemsFragment.layoutInflater.inflate(R.layout.items_filter, null)

        rentalBasisFilterLayout = filterDialogView.findViewById(R.id.rental_basis_filter_layout)
        rentalBasisInput = filterDialogView.findViewById(R.id.rental_basis_filter_spinner)
        rentalBasisInput.adapter = ItemUtils.getRentalBasisSpinnerAdapter(itemsFragment.requireContext())

        categoryInput = filterDialogView.findViewById(R.id.category_filter_spinner)
        categoryInput.adapter = ItemUtils.getCategorySpinnerAdapter(itemsFragment.requireContext(), false)
        zipCodeInput = filterDialogView.findViewById(R.id.zip_code_filter_input)

        clearFilters = filterDialogView.findViewById(R.id.clear_all_filters)

        closeButton = filterDialogView.findViewById(R.id.filter_dialog_close_button)
        closeButton.setOnClickListener {
            filterDialog.dismiss()
        }

        if (!Places.isInitialized()) {
            Places.initialize(itemsFragment.requireContext(), "AIzaSyC_DfrZTQGTxzVzLOuPKQvMHgB8ffmSVDE");
        }
        val placesClient = Places.createClient(itemsFragment.requireContext())

        addressInput = itemsFragment.childFragmentManager.findFragmentById(R.id.location_filter_autocomplete)
                as AutocompleteSupportFragment
        addressInput.setHint(locationHint)
        val searchIcon : View = addressInput.requireView().findViewById(R.id.places_autocomplete_search_button)
        searchIcon.visibility = View.GONE
        // Specify the types of place data to return.
        addressInput.setPlaceFields(listOf(Place.Field.ADDRESS))
        addressInput.setTypeFilter(TypeFilter.CITIES)
        addressInput.setCountries("US", "IND")
        // Set up a PlaceSelectionListener to handle the response.
        addressInput.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.address?.let { address ->
                    val addressSpannable = ItemUtils.getLocationText(address)
                    addressInput.setHint(addressSpannable)
                    setAddressFilters(address)
                }
            }
            override fun onError(status: Status) {
                clearAddressFilters()
            }
        })

        clearFilters.setOnClickListener {
            clearZipCodeFilters()
            clearCategoryFilters()
            clearAddressFilters()
            addressInput.setHint(locationHint)
            shouldReloadItems = true
            filterDialog.dismiss()
        }

        applyFiltersButton = filterDialogView.findViewById(R.id.apply_filters)
        applyFiltersButton.setOnClickListener {

            setZipCodeFilter()
            setCategoryFilter()
            // address filter is set when place is selected

            shouldReloadItems = true
            filterDialog.dismiss()
        }

        filterDialog.setView(filterDialogView)
    }

    fun getDialog() : AlertDialog {
        return filterDialog
    }

    fun getCategoriesFilter(): HashSet<String> {
        return categoriesFilter
    }

    fun getCitiesFilter(): HashSet<String> {
        return citiesFilter
    }

    fun getStatesFilter(): HashSet<String> {
        return statesFilter
    }

    fun getZipCodesFilter(): HashSet<String> {
        return zipcodesFilter
    }

    fun getCountriesFilter() : HashSet<String> {
        return countriesFilter
    }

    fun showRentalBasis(isShown: Boolean) {
        rentalBasisFilterLayout.visibility =  if(isShown) View.VISIBLE else View.GONE
    }

    fun isReloadItemsRequired() = shouldReloadItems

    fun reloadComplete() {
        shouldReloadItems = false
    }


    fun clearAddressFilters() {
        citiesFilter.clear()
        statesFilter.clear()
        countriesFilter.clear()
    }

    fun setAddressFilters(addressText : String) {
        clearAddressFilters()
        var address = addressText.split(",")
        citiesFilter.add(address[0])
        statesFilter.add(address[1])
        countriesFilter.add(address[2])
    }

    fun setCategoryFilter() {
        categoriesFilter.clear()
        if (categoryInput.selectedItemPosition != 0) {
            categoriesFilter.add(categoryInput.selectedItem.toString())
        }
    }

    fun clearCategoryFilters() {
        categoriesFilter.clear()
        categoryInput.setSelection(0)
    }

    fun setZipCodeFilter() {
        zipcodesFilter.clear()
        if (zipCodeInput.text.toString().isNotEmpty()) {
            zipcodesFilter.add(zipCodeInput.text.toString())
        }
    }
    fun clearZipCodeFilters() {
        zipCodeInput.text = null
        zipcodesFilter.clear()
    }

}