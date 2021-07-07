package com.getitcheap.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.getitcheap.R
import com.getitcheap.data.SharedPrefs
import com.getitcheap.utils.Utils
import com.getitcheap.web_api.RetroFitService
import com.getitcheap.web_api.RetroFitService.userApi
import com.getitcheap.web_api.api_definition.UsersApi
import com.getitcheap.web_api.request.SigninRequest
import com.getitcheap.web_api.request.SignupRequest
import com.getitcheap.web_api.response.MessageResponse
import com.getitcheap.web_api.response.SigninResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
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

    lateinit var sharedPrefsInstance : SharedPrefs
    lateinit var firstNameInputLayout : TextInputLayout
    lateinit var lastNameInputLayout : TextInputLayout
    lateinit var emailInput : TextInputEditText
    lateinit var passwordInput : TextInputEditText
    lateinit var buttonTop : MaterialButton
    lateinit var buttonBottom : MaterialButton
    lateinit var onSignIn : View.OnClickListener
    lateinit var onSignUp : View.OnClickListener
    lateinit var labelForButtonBottom : MaterialTextView
    lateinit var profileLayout : LinearLayout
    lateinit var signInSignOutLayout: ConstraintLayout
    lateinit var signOutButton: MaterialButton

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
        signInSignOutLayout = view.findViewById(R.id.signin_singout_layout)
        profileLayout = view.findViewById(R.id.profile_layout)
        sharedPrefsInstance = SharedPrefs.getInstance(view.context)

        emailInput = view.findViewById(R.id.category_input)
        passwordInput = view.findViewById(R.id.price_input)
        buttonTop = view.findViewById(R.id.button_top)
        buttonBottom = view.findViewById(R.id.button_bottom)
        labelForButtonBottom = view.findViewById(R.id.label_for_button_bottom)
        firstNameInputLayout = view.findViewById(R.id.item_name_input_layout)
        lastNameInputLayout = view.findViewById(R.id.description_input_layout)
        signOutButton = view.findViewById(R.id.profile_signout)

        // set up Listeners
        onSignIn = View.OnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            userApi.Signin(SigninRequest(email = email, password = password))
                .enqueue(object: Callback<SigninResponse> {
                    override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                        Utils.showSnackBarForFailure(view, "Sign in failed")
                    }
                    override fun onResponse(call: Call<SigninResponse>, response: Response<SigninResponse>) {
                        if (response.code() == 200) {
                            Utils.showSnackBarForSuccess(view, "Sign in successful")
                        } else {
                            Utils.showSnackBarForFailure(view, "Sign in failed")
                        }
                        val signInResponse = response.body()
                        val token = signInResponse?.jwt
                        val username = signInResponse?.username
                        val email = signInResponse?.email
                        val userId = signInResponse?.userId
                        val sharedPrefs = SharedPrefs.getInstance(view.context)
                        token?.let { sharedPrefs.setJwtToken(token) }
                        username?.let { sharedPrefs.setUsername(username) }
                        email?.let { sharedPrefs.setEmail(email) }
                        userId?.let { sharedPrefs.setUserId(userId) }
                        updateLayout()
                    }
            })

        }

        onSignUp = View.OnClickListener {
            val firstName = firstNameInputLayout.editText?.text.toString()
            val lastName = lastNameInputLayout.editText?.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            userApi.Signup(SignupRequest(firstName=firstName,
                lastName = lastName, email = email, password = password))
                .enqueue(object: Callback<MessageResponse>{
                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        Utils.showSnackBarForFailure(view, "Sign up Failed")
                    }
                    override fun onResponse(
                        call: Call<MessageResponse>,
                        response: Response<MessageResponse>
                    ) {
                        if (response.code() == 200) {
                            Utils.showSnackBarForSuccess(view, "Sign up successful")
                            setupViewForSignIn()
                        } else {
                            Utils.showSnackBarForFailure(view, "Sign up Failed")
                        }
                    }
                })
        }

        signOutButton.setOnClickListener {
            sharedPrefsInstance.clearAll()
            updateLayout()
        }

        updateLayout()

    }


    private fun setupViewForSignIn() {
        firstNameInputLayout.visibility = View.GONE
        lastNameInputLayout.visibility = View.GONE
        buttonTop.text = "Sign In"
        labelForButtonBottom.text = "Don't have an account ?"
        buttonBottom.text = "Sign Up"
        buttonTop.setOnClickListener(onSignIn)
        buttonBottom.setOnClickListener {
            setupViewForSignUp()
        }
    }

    private fun setupViewForSignUp() {
        firstNameInputLayout.visibility = View.VISIBLE
        lastNameInputLayout.visibility = View.VISIBLE
        buttonTop.text = "Sign Up"
        labelForButtonBottom.text = "Already have an account ?"
        buttonBottom.text = "Sign In"
        buttonTop.setOnClickListener(onSignUp)
        buttonBottom.setOnClickListener {
            setupViewForSignIn()
        }
    }

    fun updateLayout() {
        val isLoggedIn = sharedPrefsInstance.getEmail().isNotEmpty()
        if (isLoggedIn) {
            profileLayout.visibility = View.VISIBLE
            signInSignOutLayout.visibility = View.GONE
        } else {
            profileLayout.visibility = View.GONE
            signInSignOutLayout.visibility = View.VISIBLE
            setupViewForSignIn() // By default show SignIn view
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