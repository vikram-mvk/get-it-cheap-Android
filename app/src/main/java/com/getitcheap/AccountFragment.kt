package com.getitcheap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.getitcheap.web_api.RetroFitService
import com.getitcheap.web_api.api_definition.UsersApi
import com.getitcheap.web_api.request.SigninRequest
import com.getitcheap.web_api.response.SigninResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var emailInput : TextInputEditText
    lateinit var passwordInput : TextInputEditText
    lateinit var signinButton : MaterialButton

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
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Find views
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        signinButton = view.findViewById(R.id.sign_in_button)

        // Setup up Api Requests
        val userApi = RetroFitService.useApi(UsersApi::class.java)

        // Do stuff with views
        signinButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val signInRequest = userApi.Signin(SigninRequest(email = email, password = password))
            println(email)
            println(password)
            signInRequest.enqueue(object: Callback<SigninResponse>{
                override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                    Log.d("Signin Response", "failure")
                }

                override fun onResponse(call: Call<SigninResponse>, response: Response<SigninResponse>) {
                    Log.d("Signin Response", "success")
                    val signInResponse = response.body()
                    println(signInResponse?.jwt)
                }

            })
        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}