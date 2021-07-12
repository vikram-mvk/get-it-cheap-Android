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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddNewItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNewItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
    var imageFile : File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefsInstance = SharedPrefs.getInstance(view.context)

        // Link the views
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
                requestPermissions(
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    2000);
            }
            else {
                openGallery();
            }

        }


        submitYourItem.setOnClickListener {
                uploadNewItem()
        }

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

        itemsApi.newItem(token = token, itemName = itemName, description = description, price = price,
            category = category, itemType = type, rentalBasis = rentalBasisValue, userId = userId, username = username,
            contact = contact, image = imageMultiPart)
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListYourItem.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNewItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}