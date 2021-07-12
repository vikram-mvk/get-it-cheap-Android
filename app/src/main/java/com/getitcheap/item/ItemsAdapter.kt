package com.getitcheap.item

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.getitcheap.BaseActivity
import com.getitcheap.R
import com.getitcheap.utils.FragmentUtils
import com.getitcheap.utils.ItemUtils
import com.getitcheap.web_api.response.ItemsResponse

class ItemsAdapter(private var items: List<ItemsResponse>) : RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    val S3_BASE_URL = "https://get-it-cheap.s3.amazonaws.com/"
    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemNameTextView : TextView =  itemView.findViewById(R.id.item_name_text)
        private val itemDescriptionTextView: TextView = itemView.findViewById(R.id.item_description_text)
        private val itemPriceTextView : TextView = itemView.findViewById(R.id.item_price_text)
        private val itemTypeTextView: TextView = itemView.findViewById(R.id.item_type_text)
        private val rentalBasisTextView : TextView = itemView.findViewById(R.id.item_rental_basis_text)
        private val itemImage: ImageView = itemView.findViewById(R.id.item_image)

        fun bind(item: ItemsResponse) {
            itemNameTextView.text = item.itemName
            itemDescriptionTextView.text = item.description
            itemPriceTextView.text = String.format("$%s", item.price)
            itemTypeTextView.text = ItemUtils.getItemTypeDisplayString(item.itemType)

            if (ItemUtils.isForRent(item.itemType)) {
                rentalBasisTextView.text = ItemUtils.getRentalBasisDisplayString(item.rentalBasis!!)
                rentalBasisTextView.visibility = View.VISIBLE
            }

            Glide.with(itemView.context)
                .load(S3_BASE_URL+item.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(itemImage)

            itemView.setOnClickListener {
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, 0,0,
                        android.R.anim.slide_out_right)
                    .replace(R.id.base_fragment_container, ItemDetailsFragment(items[position]))
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

