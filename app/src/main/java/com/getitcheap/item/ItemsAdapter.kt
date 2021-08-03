package com.getitcheap.item

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import com.getitcheap.web_api.RetroFitService.itemsApi
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Callback
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.getitcheap.BaseActivity
import com.getitcheap.R
import com.getitcheap.utils.FragmentUtils
import com.getitcheap.utils.ItemUtils
import com.getitcheap.utils.Utils
import com.getitcheap.web_api.request.DeleteItemsRequest
import com.getitcheap.web_api.response.ItemsResponse
import com.getitcheap.web_api.response.MessageResponse
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Response

class ItemsAdapter(private var items: List<ItemsResponse>, private var isProfileItems: Boolean,
                   private var token:String) : RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemNameTextView : TextView =  itemView.findViewById(R.id.item_name_text)
        private val itemDescriptionTextView: TextView = itemView.findViewById(R.id.item_description_text)
        private val itemPriceTextView : TextView = itemView.findViewById(R.id.item_price_text)
        private val itemCard : MaterialCardView = itemView.findViewById(R.id.card)
        private val itemTypeTextView: TextView = itemView.findViewById(R.id.item_type_text)
        private val rentalBasisTextView : TextView = itemView.findViewById(R.id.item_rental_basis_text)
        private val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(item: ItemsResponse) {

            itemNameTextView.text = item.itemName
            itemDescriptionTextView.text = item.description
            itemPriceTextView.text = String.format("$%s", item.price)
            itemTypeTextView.text = ItemUtils.getItemTypeDisplayString(item.itemType)

            if (isProfileItems) {
                val params = itemCard.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(5)
                itemCard.layoutParams = params

                itemNameTextView.setPadding(1, 2, 5, 2)
                itemNameTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                itemDescriptionTextView.visibility = View.GONE
                itemImage.visibility = View.GONE
                deleteButton.visibility = View.VISIBLE
                itemTypeTextView.visibility = View.GONE
                itemPriceTextView.visibility = View.GONE
                deleteButton.setOnClickListener {
                    val itemIds = ArrayList<Long>()
                    itemIds.add(item.id)
                    val deleteRequest = DeleteItemsRequest(itemIds)
                    itemsApi.deleteItems(token = token, request = deleteRequest)
                    .enqueue(object : Callback<MessageResponse> {
                        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                            if(response.isSuccessful) {
                                val messageResponse : MessageResponse = response.body()!!
                                val updatedItemList = (items as ArrayList<ItemsResponse>)
                                updatedItemList.removeAt(absoluteAdapterPosition)
                                setData(updatedItemList)
                                Utils.showSnackBarForSuccess(itemView, messageResponse.message)
                            }
                        }

                        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                            Utils.showSnackBarForFailure(itemView,
                                itemView.context.getString(R.string.error_communicating_with_server))
                        }

                    }
                    )
                }
                return
            }

            if (ItemUtils.isForRent(item.itemType)) {
                rentalBasisTextView.text = ItemUtils.getRentalBasisDisplayString(item.rentalBasis!!)
                rentalBasisTextView.visibility = View.VISIBLE
            }



            Glide.with(itemView.context)
                .load(ItemUtils.S3_BASE_URL+item.image)
                .placeholder(R.drawable.no_image_available_icon)
                .error(R.drawable.no_image_available_icon)
                .into(itemImage)

            itemView.setOnClickListener {
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, 0,0,
                        android.R.anim.slide_out_right)
                    .replace(R.id.base_fragment_container, ItemDetailsFragment(items[absoluteAdapterPosition]))
                    .addToBackStack(FragmentUtils.ITEM_DETAILS_FRAGMENT)
                    .commit()
            }
        }
    }

    fun setData(newItems : List<ItemsResponse>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
       val view: View = LayoutInflater.from(parent.context).inflate(R.layout.items_recycler_view, parent, false)
        return ItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(itemsViewholder: ItemsViewHolder, position: Int) {
        itemsViewholder.bind(items[position])
    }

}

