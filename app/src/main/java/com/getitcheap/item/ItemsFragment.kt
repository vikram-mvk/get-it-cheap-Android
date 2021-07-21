package com.getitcheap.item

import android.content.ClipData
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getitcheap.R
import com.getitcheap.utils.Utils
import com.getitcheap.utils.ItemUtils
import com.getitcheap.web_api.RetroFitService.itemsApi
import com.getitcheap.web_api.response.ItemsResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemsFragment : Fragment() {

    private var fragmentView : View? = null
    private var itemTypeFilters = HashSet<String>();
    private var categoryFilters = HashSet<String>();
    lateinit var itemsLoadingLayout: LinearLayout
    lateinit var itemsRecyclerView: RecyclerView
    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var itemsResponseTextView: MaterialTextView
    lateinit var checkboxForRent: MaterialCheckBox
    lateinit var checkboxForSale: MaterialCheckBox
    lateinit var sortButton: MaterialButton
    lateinit var filterButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // If we have arguments, it goes here
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Find views
        searchView = view.findViewById(R.id.search_input)
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view)
        itemsLoadingLayout = view.findViewById(R.id.items_loading_layout)
        itemsResponseTextView = view.findViewById(R.id.items_response_text_view)
        checkboxForRent = view.findViewById(R.id.checkbox_for_rent)
        checkboxForSale = view.findViewById(R.id.checkbox_for_sale)

        // Initialize the recycler view
        itemsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(view.context)
            adapter = ItemsAdapter(ArrayList<ItemsResponse>()) // Initially put a empty array
        }

        // Initially both are checked, add them in the set by default
        itemTypeFilters.add(ItemUtils.FOR_RENT)
        itemTypeFilters.add(ItemUtils.FOR_SALE)

        getItems(view) // gets All Items and populates the recycler view with items

        checkboxForSale.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) itemTypeFilters.add(ItemUtils.FOR_SALE) else itemTypeFilters.remove(
                ItemUtils.FOR_SALE
            )
            getItems(view)
        }

        checkboxForRent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) itemTypeFilters.add(ItemUtils.FOR_RENT) else itemTypeFilters.remove(
                ItemUtils.FOR_RENT
            )
            getItems(view)
        }

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchKey = searchView.query
                if (searchKey.isNotEmpty()) {
                    searchItems(view, searchKey.toString())
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onResume() {
        super.onResume()
    }

    fun getItems(view: View) {

        itemsLoadingLayout.visibility = View.VISIBLE

        var itemTypesQueryString: String? = ""
        var categoryQueryString: String? = ""
        val queryFormat = ",%s"

        itemTypeFilters.forEach { type -> itemTypesQueryString += String.format(queryFormat, type) }
        categoryFilters.forEach { category ->
            categoryQueryString += String.format(
                queryFormat,
                category
            )
        }

        itemTypesQueryString =
            if (itemTypesQueryString!!.isEmpty()) "none" else itemTypesQueryString.substring(1)
        categoryQueryString =
            if (categoryQueryString!!.isEmpty()) null else categoryQueryString.substring(1)

        itemsApi.getItems(itemTypesQueryString, categoryQueryString)
            .enqueue(object : Callback<List<ItemsResponse>> {

                override fun onFailure(call: Call<List<ItemsResponse>>, t: Throwable) {
                    itemsLoadingLayout.visibility = View.GONE
                    val errorMessage = view.context?.getString(R.string.error_getting_items)
                    itemsResponseTextView.text = errorMessage
                    itemsResponseTextView.visibility = View.VISIBLE
                    Utils.showSnackBarForFailure(view, errorMessage)
                }

                override fun onResponse(
                    call: Call<List<ItemsResponse>>,
                    response: Response<List<ItemsResponse>>
                ) {
                    itemsLoadingLayout.visibility = View.GONE

                    if (response.code() != 200) Utils.showSnackBarForFailure(view, "Error getting Items from Server")

                    var items: List<ItemsResponse>? = response.body()

                    if (items == null || items.isEmpty()) {
                        itemsRecyclerView.visibility = View.GONE
                        itemsResponseTextView.text = getString(R.string.no_items_available)
                        itemsResponseTextView.visibility = View.VISIBLE
                    } else {
                        itemsRecyclerView.adapter?.let { adapter ->
                            (adapter as ItemsAdapter).setData(items)
                        }
                        itemsResponseTextView.visibility = View.GONE
                        itemsRecyclerView.visibility = View.VISIBLE
                    }
                }
            })
    }

    fun searchItems(view: View, searchKey: String) {

        itemsLoadingLayout.visibility = View.VISIBLE

        itemsApi.searchItems(searchKey)
            .enqueue(object : Callback<List<ItemsResponse>> {

                override fun onFailure(call: Call<List<ItemsResponse>>, t: Throwable) {
                    itemsLoadingLayout.visibility = View.GONE
                    Utils.showSnackBarForFailure(view, "Error getting Items from Server")
                }

                override fun onResponse(
                    call: Call<List<ItemsResponse>>,
                    response: Response<List<ItemsResponse>>
                ) {
                    itemsLoadingLayout.visibility = View.GONE

                    if (response.code() != 200) Utils.showSnackBarForFailure(
                        view,
                        "Error getting Items from Server"
                    )

                    var items: List<ItemsResponse>? = response.body()


                    if (items == null || items.isEmpty()) {
                        itemsRecyclerView.visibility = View.GONE
                        itemsResponseTextView.visibility = View.VISIBLE
                    } else {
                        itemsRecyclerView.adapter?.let { adapter ->
                            (adapter as ItemsAdapter).setData(items)
                        }
                        itemsResponseTextView.visibility = View.GONE
                        itemsRecyclerView.visibility = View.VISIBLE
                    }
                }
            })
    }

    companion object {

        @Volatile private var itemsFragment :ItemsFragment? = null

        fun getInstance(): ItemsFragment =  itemsFragment ?: synchronized(this) {
            itemsFragment ?: ItemsFragment().also { it -> itemsFragment = it }
        }
    }

}