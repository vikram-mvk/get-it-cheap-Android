package com.getitcheap.item

import android.Manifest
import android.content.DialogInterface
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
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.getitcheap.BaseActivity
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    lateinit var sharedPrefsInstance: SharedPrefs
    lateinit var itemName: TextInputEditText
    lateinit var description: TextInputEditText
    lateinit var category: Spinner
    lateinit var itemType: RadioGroup
    lateinit var price: TextInputEditText
    lateinit var rentalBasis: Spinner
    lateinit var addressInput: AutocompleteSupportFragment
    lateinit var rentalBasisLayout: LinearLayout
    lateinit var contact: TextInputEditText
    lateinit var submitYourItem: MaterialButton
    lateinit var uploadImage: MaterialButton
    lateinit var imageName: TextView

    var address: String = ""
    var imageFile: File? = null


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
        imageName = view.findViewById(R.id.image_name)
        submitYourItem = view.findViewById(R.id.submit_your_item)
        addressInput = childFragmentManager.findFragmentById(R.id.address_places_api) as AutocompleteSupportFragment

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyC_DfrZTQGTxzVzLOuPKQvMHgB8ffmSVDE");
        }

        val placesClient = Places.createClient(requireContext())


        addressInput.setHint(requireContext().getString(R.string.enter_item_location))
        val searchIcon : View = addressInput.requireView().findViewById(R.id.places_autocomplete_search_button)
        searchIcon.visibility = View.GONE
        // Specify the types of place data to return.
        addressInput.setPlaceFields(listOf(Place.Field.ADDRESS))
        addressInput.setTypeFilter(TypeFilter.ADDRESS)
        addressInput.setCountries("US", "IND")

        // Set up a PlaceSelectionListener to handle the response.
        addressInput.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                address = place.address!!
                addressInput.setHint(ItemUtils.getLocationText(address))
            }

            override fun onError(status: Status) {}
        })


        // Set up adapters for spinner
        category.adapter = ItemUtils.getCategorySpinnerAdapter(view.context, true)
        rentalBasis.adapter = ItemUtils.getRentalBasisSpinnerAdapter(view.context)

        itemType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.item_type_sale -> rentalBasisLayout.visibility = View.GONE
                else -> rentalBasisLayout.visibility = View.VISIBLE
            }
        }

        uploadImage.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(view.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE), 2000);
            } else {
                openGallery();
            }
        }

        submitYourItem.setOnClickListener {
            if (!checkInputValidity()) {
                Utils.showSnackBarForFailure(requireView(), requireContext().getString(R.string.enter_valid_input))
                return@setOnClickListener
            }
            uploadNewItem()
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        address = ""
        imageFile = null
       
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            try {
                var path = data.data?.let { filePath -> return@let getPath(filePath) }
                imageFile = File(path)
                if (imageFile != null) {
                    val fileSizeInBytes: Long = imageFile!!.length()
                    val fileSizeInKB = fileSizeInBytes / 1024
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB > 5) {
                        Utils.showSnackBarForFailure(requireView(), "Maximum allowed file size is 5 MB")
                        imageFile = null
                        return@onActivityResult
                    } else {
                        imageName.text = imageFile!!.name
                    }
                } else {
                    Utils.showSnackBarForFailure(requireView(), "Error uploading selected image")
                }
            } catch (e: Exception) {
                   println(e.message)
            }
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

    private fun clearAllInput() {
        itemName.text = null
        description.text = null
        category.setSelection(0)
        itemType.clearCheck()
        rentalBasis.setSelection(0)
        address = ""
        addressInput.setHint(requireContext().getString(R.string.enter_item_location))
        imageFile = null
        imageName.text = null
        price.text = null
        contact.text = null

    }

    private fun uploadNewItem() {
        val uploadingAlert = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Please Wait")
            .setMessage("Submitting item..").show()
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
                    if (response.isSuccessful) {
                        uploadingAlert.dismiss()
                        val newItemResponse = response.body()
                        println(newItemResponse?.message)
                        Utils.showSnackBarForSuccess(view!!, "Your item has been posted!")
                        clearAllInput()
                        BaseActivity.switchPage(requireContext(), R.id.navbar_items)
                    } else {
                        Utils.showSnackBarForFailure(view!!, "Error posting item.")
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    uploadingAlert.dismiss()
                    Utils.showSnackBarForFailure(view!!, "Error posting item.")
                    Log.d("err", t.message!!)
                }
            })

    }

    private fun checkInputValidity() : Boolean{
        return itemName.text.toString().isNotEmpty() && description.text!!.isNotEmpty() && contact.text!!.isNotEmpty() &&
        price.text!!.isNotEmpty() && address.isNotEmpty()
    }

    private fun openGallery() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            Utils.showSnackBarForFailure(requireView(), "Please grant permissions to gallery to upload image")
        }
        val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        cameraIntent.type = "image/*"
        startActivityForResult(cameraIntent, 1000)
    }

    companion object {
        fun newInstance() = AddNewItemFragment()
    }
}