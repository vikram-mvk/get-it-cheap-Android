package com.getitcheap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getitcheap.web_requests.RetroFitService
import com.getitcheap.web_requests.items.ItemsApi
import com.getitcheap.web_requests.items.ItemsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ItemsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemsFragment : Fragment() {

    private var param1: String? = null
    lateinit var itemsRecyclerView: RecyclerView
    lateinit var searchLayout: LinearLayout
    lateinit var searchView: androidx.appcompat.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
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
        // Find all views
        searchLayout = view.findViewById(R.id.search_layout)
        searchView = view.findViewById(R.id.search_input)
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view)

        // Set up actions
        searchLayout.setOnClickListener {
            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
            searchView.onActionViewExpanded()
        }

        val itemsApi = RetroFitService.useApi(ItemsApi::class.java)
        val getAllItemsRequest = itemsApi.getAllItems()
        val getAllItemsResponse = getAllItemsRequest.enqueue(object: Callback<List<ItemsModel>> {
            override fun onFailure(call: Call<List<ItemsModel>>, t: Throwable) {
                Log.d("Response", "failure")
            }

            override fun onResponse(
                call: Call<List<ItemsModel>>,
                response: Response<List<ItemsModel>>
            ) {
                itemsRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(view.context)
                    adapter = ItemsAdapter(response.body()!!)




                }
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
         * @return A new instance of fragment ItemsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}