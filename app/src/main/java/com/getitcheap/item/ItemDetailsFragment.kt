package com.getitcheap.item

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.getitcheap.R
import com.getitcheap.utils.FragmentUtils
import com.getitcheap.utils.ItemUtils
import com.getitcheap.utils.Utils
import com.getitcheap.web_api.response.ItemsResponse

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ITEM ="item"

/**
 * A simple [Fragment] subclass.
 * Use the [ItemDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemDetailsFragment(private val item: ItemsResponse) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var itemName : TextView
    private lateinit var itemType : TextView
    private lateinit var itemPrice : TextView
    private lateinit var itemRentalBasis : TextView
    private lateinit var itemDescription : TextView
    private lateinit var itemContact : TextView
    private lateinit var itemLocation : TextView
    private lateinit var itemUsername : TextView
    private lateinit var itemCategory : TextView
    private lateinit var itemDatePosted : TextView
    private lateinit var itemImage1 : ImageView
    private val S3_BASE_URL = "https://get-it-cheap.s3.amazonaws.com/"



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
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemName = view.findViewById(R.id.item_details_name)
        itemType = view.findViewById(R.id.item_details_item_type)
        itemPrice = view.findViewById(R.id.item_details_price)
        itemRentalBasis = view.findViewById(R.id.item_details_rental_basis)
        itemDescription = view.findViewById(R.id.item_details_description)
        itemLocation = view.findViewById(R.id.item_details_item_location)
        itemContact = view.findViewById(R.id.item_details_contact)
        itemUsername = view.findViewById(R.id.item_details_username)
        itemCategory = view.findViewById(R.id.item_details_category)
        itemDatePosted = view.findViewById(R.id.item_details_date_posted)
        itemImage1 = view.findViewById(R.id.item_image_1)

        itemName.text = item.itemName
        itemType.text = ItemUtils.getItemTypeDisplayString(item.itemType)
        itemPrice.text = ItemUtils.getPriceDisplayString(item.price, item.itemLocation)

        if (ItemUtils.isForRent(item.itemType)) {
            itemRentalBasis.text = ItemUtils.getRentalBasisDisplayString(item.rentalBasis!!)
            itemRentalBasis.visibility = View.VISIBLE
        }

        Glide.with(view.context)
            .load(S3_BASE_URL+item.image)
            .placeholder(R.drawable.no_image_available_icon)
            .error(R.drawable.no_image_available_icon)
            .into(itemImage1)

        itemImage1.setOnClickListener {
            val intent = Intent(this.context, ImageFullscreenViewActivity::class.java)
            intent.putExtra("IMAGE_URL", S3_BASE_URL+item.image)
            startActivity(intent)
        }

        itemDescription.text = item.description
        itemLocation.text = item.itemLocation
        itemContact.text = item.contact
        itemUsername.text = item.username
        itemCategory.text = item.category
        itemDatePosted.text = item.datePosted
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ItemDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(item : ItemsResponse) =
            ItemDetailsFragment(item).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}