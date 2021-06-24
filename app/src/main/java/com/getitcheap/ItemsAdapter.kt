package com.getitcheap

import android.content.Context
import android.service.autofill.TextValueSanitizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.getitcheap.web_requests.items.ItemsModel

class ItemsAdapter(private val items: List<ItemsModel>) : RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder>() {

    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView : TextView =  itemView.findViewById(R.id.item_name_text)
        private val itemDescriptionTextView: TextView = itemView.findViewById(R.id.item_description_text)
        private val itemPriceTextView : TextView = itemView.findViewById(R.id.item_price_text)
        private val itemImage: ImageView = itemView.findViewById(R.id.item_image)

        fun bind(item: ItemsModel) {
            itemNameTextView.text = item.itemName
            itemDescriptionTextView.text = item.description
            itemPriceTextView.text = item.price
            Glide.with(itemView.context).load(item.image).into(itemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
       val view: View = LayoutInflater.from(parent.context).inflate(R.layout.items_recycler_view, parent, false)
        return ItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    override fun onBindViewHolder(itemsViewholder: ItemsViewHolder, position: Int) {
        itemsViewholder.bind(items[position])
    }

}
