package com.getitcheap.item

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.getitcheap.R
import com.getitcheap.data.SharedPrefs
import com.getitcheap.utils.ItemUtils
import com.getitcheap.utils.Utils
import com.getitcheap.web_api.RetroFitService.itemsApi
import com.getitcheap.web_api.response.MessageResponse
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AddNewItemFragment : Fragment() {

    lateinit var sharedPrefsInstance : SharedPrefs
    lateinit var itemName : TextInputEditText
    lateinit var description : TextInputEditText
    lateinit var category : Spinner
    lateinit var itemType : RadioGroup
    lateinit var price : TextInputEditText
    lateinit var rentalBasis : Spinner
    lateinit var rentalBasisLayout : LinearLayout
    lateinit var contact : TextInputEditText
    lateinit var submitYourItem: MaterialButton
    lateinit var uploadImage : MaterialButton
    var address : String = ""
    var imageFile : File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
          // args
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_new_item, container, false)

        sharedPrefsInstance = SharedPrefs.getInstance(view.context)
        itemName = view.findViewById(R.id.item_name_input)
        description = view.findViewById(R.id.description_input)
        category = view.findViewById(R.id.category_spinner)
        itemType = view.findViewById(R.id.item_type_radio_group)
        price = view.findViewById(R.id.price_input)
        rentalBasisLayout = view.findViewById(R.id.rental_basis_layout)
        rentalBasis = view.findViewById(R.id.rental_basis_spinner)
        contact = view.findViewById(R.id.contact_input)
        uploadImage = view.findViewById(R.id.upload_image)
        submitYourItem = view.findViewById(R.id.submit_your_item)

        // Set up adapters for spinner
        val categories = listOf("Electronics", "Outdoor", "Clothing")
        category.adapter = ArrayAdapter(view.context, R.layout.getitcheap_spinner, categories)

        rentalBasis.adapter = ArrayAdapter(view.context, R.layout.getitcheap_spinner,
            ItemUtils.rentalBasisDisplayStringToDbString.keys.toTypedArray())

        itemType.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.item_type_sale -> rentalBasisLayout.visibility = View.GONE
                else -> rentalBasisLayout.visibility = View.VISIBLE
            }
        }

        uploadImage.setOnClickListener {

            if(ActivityCompat.checkSelfPermission(view.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE), 2000);
            }
            else {
                openGallery();
            }

        }
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyC_DfrZTQGTxzVzLOuPKQvMHgB8ffmSVDE");
        }

        val placesClient = Places.createClient(requireContext())

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.address_places_api) as AutocompleteSupportFragment
        autocompleteFragment.setHint("Enter Item location")

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS))
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS)
        autocompleteFragment.setCountry("US")

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                address = place.address!!
                autocompleteFragment.setHint(address)
            }

            override fun onError(status: Status) {

            }
        })

        submitYourItem.setOnClickListener {
            if (!checkInputValidity()) {
                Utils.showSnackBarForFailure(requireView(), "Please fill out all the fields")
                return@setOnClickListener
            }
            uploadNewItem()
        }
        return view
    }

    private fun openGallery() {
        val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        cameraIntent.type = "image/*"
        startActivityForResult(cameraIntent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            val uri =  data.data
            imageFile = File(uri?.let { fileUri -> getPath(fileUri) }!!)
        }
    }

    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaColumns.DATA)
        val cursor: Cursor? = context?.contentResolver?.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaColumns.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex!!)
        cursor?.close()
        return path
    }

    private fun uploadNewItem() {

        val itemName = itemName.text.toString().toRequestBody(MultipartBody.FORM)
        val description = description.text.toString().toRequestBody(MultipartBody.FORM)
        val category = category.selectedItem.toString().toRequestBody(MultipartBody.FORM)
        val type = ItemUtils.getItemTypeDbString(itemType.checkedRadioButtonId)
            .toRequestBody(MultipartBody.FORM)
        val price = price.text.toString().toRequestBody(MultipartBody.FORM)
        var rentalBasisValue : RequestBody? = null

        if (ItemUtils.isForRent(ItemUtils.getItemTypeDbString(itemType.checkedRadioButtonId))) {
            rentalBasisValue = ItemUtils.getRentalBasisDbString(rentalBasis.selectedItem.toString())
                .toRequestBody(MultipartBody.FORM)
        }
        var imageMultiPart : MultipartBody.Part? = null

        if (imageFile != null) {
            imageMultiPart = MultipartBody.Part.createFormData("image", imageFile!!.name,
                imageFile!!.asRequestBody(MultipartBody.FORM))
        }

        val contact = contact.text.toString().toRequestBody(MultipartBody.FORM)
        val username = sharedPrefsInstance.getUsername().toRequestBody(MultipartBody.FORM)
        val userId = sharedPrefsInstance.getUserId().toString().toRequestBody(MultipartBody.FORM)
        val token = sharedPrefsInstance.getJwtToken()
        val itemLocation = address.toRequestBody(MultipartBody.FORM)

        itemsApi.newItem(token = token, itemName = itemName, description = description, price = price,
            category = category, itemType = type, rentalBasis = rentalBasisValue, userId = userId, username = username,
            contact = contact, itemLocation = itemLocation, image = imageMultiPart)
            .enqueue(object: Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    val newItemResponse = response.body()
                    println(newItemResponse?.message)
                    Utils.showSnackBarForSuccess(view!!, "Your item has been posted!")
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Utils.showSnackBarForFailure(view!!, "Error in posting Item.")
                    Log.d("err", t.message!!)
                }
            })

    }


    fun checkInputValidity() : Boolean{
        return itemName.text.toString().isNotEmpty() && description.text!!.isNotEmpty() && contact.text!!.isNotEmpty() &&
        price.text!!.isNotEmpty() && address.isNotEmpty()
    }


    companion object {
        @Volatile private var addNewItemFragment :AddNewItemFragment? = null

        fun getInstance(): AddNewItemFragment =  addNewItemFragment ?: synchronized(this) {
            addNewItemFragment ?: AddNewItemFragment().also { it -> addNewItemFragment = it }
        }
    }
}