package com.getitcheap.user

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getitcheap.R
import com.getitcheap.data.SharedPrefs
import com.getitcheap.item.ItemsAdapter
import com.getitcheap.item.ShowAddButton
import com.getitcheap.utils.AccountUtils
import com.getitcheap.utils.Utils
import com.getitcheap.web_api.RetroFitService.itemsApi
import com.getitcheap.web_api.RetroFitService.userApi
import com.getitcheap.web_api.request.SigninRequest
import com.getitcheap.web_api.request.SignupRequest
import com.getitcheap.web_api.request.UpdateProfileRequest
import com.getitcheap.web_api.response.ItemsResponse
import com.getitcheap.web_api.response.MessageResponse
import com.getitcheap.web_api.response.SigninResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AccountFragment : Fragment() {

    lateinit var sharedPrefsInstance : SharedPrefs
    lateinit var firstNameInputLayout : TextInputLayout
    lateinit var firstNameInput : TextInputEditText
    lateinit var lastNameInputLayout : TextInputLayout
    lateinit var lastNameInput : TextInputEditText
    lateinit var emailInputLayout : TextInputLayout
    lateinit var emailInput : TextInputEditText
    lateinit var passwordInputLayout : TextInputLayout
    lateinit var passwordInput : TextInputEditText
    lateinit var buttonTop : MaterialButton
    lateinit var buttonBottom : MaterialButton
    lateinit var onSignIn : View.OnClickListener
    lateinit var onSignUp : View.OnClickListener
    lateinit var labelForButtonBottom : MaterialTextView
    lateinit var profileLayout : RelativeLayout
    lateinit var signInSignOutLayout: ConstraintLayout
    lateinit var signOutButton: MaterialButton
    lateinit var showAddButtonImpl : ShowAddButton
    lateinit var inputValidityMap : MutableMap<Int, Boolean>
    lateinit var yourItemsView : RecyclerView
    lateinit var profileFirstName : TextView
    lateinit var profileLastName : TextView
    lateinit var profileEmail : TextView
    lateinit var profileFirstNameEdit : ImageButton
    lateinit var profileLastNameEdit : ImageButton
    lateinit var profileEmailEdit : ImageButton
    lateinit var noItemsPosted : TextView


    fun TextInputLayout.setBoxColor(color: Int) {
        val defaultStrokeColor = TextInputLayout::class.java.getDeclaredField("defaultStrokeColor")
        defaultStrokeColor.isAccessible = true
        defaultStrokeColor.set(this, color)
    }

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

            val view = inflater.inflate(R.layout.fragment_account, container, false)
            signInSignOutLayout = view.findViewById(R.id.signin_signout_layout)
            profileLayout = view.findViewById(R.id.profile_layout)
            sharedPrefsInstance = SharedPrefs.getInstance(view.context)
            emailInputLayout = view.findViewById(R.id.email_input_layout)
            emailInput = view.findViewById(R.id.email_input)
            passwordInputLayout = view.findViewById(R.id.password_input_layout)
            passwordInput = view.findViewById(R.id.password_input)
            buttonTop = view.findViewById(R.id.button_top)
            buttonBottom = view.findViewById(R.id.button_bottom)
            labelForButtonBottom = view.findViewById(R.id.label_for_button_bottom)
            firstNameInputLayout = view.findViewById(R.id.first_name_input_layout)
            firstNameInput = view.findViewById(R.id.first_name_input)
            lastNameInput = view.findViewById(R.id.last_name_input)
            lastNameInputLayout = view.findViewById(R.id.last_name_input_layout)
            signOutButton = view.findViewById(R.id.profile_signout)
            yourItemsView = view.findViewById(R.id.profile_items_recycler_view)
            profileEmail = view.findViewById(R.id.profile_email)
            profileFirstName = view.findViewById(R.id.profile_firstname)
            profileLastName = view.findViewById(R.id.profile_lastname)
            profileEmailEdit = view.findViewById(R.id.profile_email_edit)
            profileFirstNameEdit = view.findViewById(R.id.profile_firstname_edit)
            profileLastNameEdit = view.findViewById(R.id.profile_lastname_edit)
            noItemsPosted = view.findViewById(R.id.no_items_posted)

            val onEditButtonClick = View.OnClickListener {
                showEditDialog(it.id)
            }
            profileFirstNameEdit.setOnClickListener(onEditButtonClick)
            profileLastNameEdit.setOnClickListener(onEditButtonClick)
            profileEmailEdit.setOnClickListener(onEditButtonClick)

            yourItemsView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ItemsAdapter(ArrayList<ItemsResponse>(), true, sharedPrefsInstance.getJwtToken())
            }

            var allInputFields = arrayOf(firstNameInput, lastNameInput, emailInput, passwordInput)

            inputValidityMap = mutableMapOf(
                firstNameInput.id to AccountUtils.getRegexForField(firstNameInput.id).matches(firstNameInput.text.toString()),
                lastNameInput.id to AccountUtils.getRegexForField(lastNameInput.id).matches(lastNameInput.text.toString()),
                emailInput.id to AccountUtils.getRegexForField(emailInput.id).matches(emailInput.text.toString()),
                passwordInput.id to AccountUtils.getRegexForField(passwordInput.id).matches(passwordInput.text.toString())
            )

            allInputFields.forEach {
                it.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        val input = it.text.toString()
                        val inputLayout = it.parent.parent as TextInputLayout
                        var isInputValid = AccountUtils.getRegexForField(it.id).matches(input)
                        var showErrorOnBox = input.isNotEmpty() && !isInputValid
                        var errorMessage = "Invalid "+ inputLayout.hint.toString()
                        // If its a password in sign in view, just check if its not empty
                        if (it.id == R.id.password_input && isSignInLayout()) {
                            isInputValid = input.isNotEmpty()
                            showErrorOnBox = input.isEmpty()
                            errorMessage = "Please enter your Password"
                        }
                        inputValidityMap[it.id] = isInputValid

                        checkInputValidity(showErrorOnBox, it.parent.parent as TextInputLayout, errorMessage)
                    }
                }
            }

            // set up Listeners
            onSignIn = View.OnClickListener {
                Utils.closeTheKeyBoard(view)

                if (!checkInputValidity(null, null, null)) {
                    Utils.showSnackBarForFailure(view, "Please enter valid input in all fields")
                    return@OnClickListener
                }

                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                val signInRequest = userApi.signin(SigninRequest(email = email, password = password))

                val signingInView = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(requireContext().getString(R.string.please_wait))
                    .setMessage("Signing In..")
                    .setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            signInRequest.cancel()
                        }
                    }).show()

                signInRequest.enqueue(object: Callback<SigninResponse> {
                    override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                        signingInView.dismiss()
                        Utils.showSnackBarForFailure(view, requireContext().getString(R.string.error_communicating_with_server))
                    }
                    override fun onResponse(call: Call<SigninResponse>, response: Response<SigninResponse>) {
                        signingInView.dismiss()
                        if (response.code() == 200) {
                            Utils.showSnackBarForSuccess(view, requireContext().getString(R.string.sign_in_successful))
                        } else {
                            val failureResponse = GsonBuilder().create().fromJson(response.errorBody()!!.string(),
                                MessageResponse::class.java)
                            Utils.showSnackBarForFailure(view, failureResponse.message)
                        }

                        val signInResponse = response.body()
                        val token = signInResponse?.jwt
                        val username = signInResponse?.username
                        val email = signInResponse?.email
                        val firstName = signInResponse?.firstName
                        val lastName = signInResponse?.lastName
                        val userId = signInResponse?.userId
                        token?.let { sharedPrefsInstance.setJwtToken(token) }
                        username?.let { sharedPrefsInstance.setUsername(username) }
                        firstName?.let { sharedPrefsInstance.setFirstName(firstName) }
                        lastName?.let { sharedPrefsInstance.setLastName(lastName) }
                        email?.let { sharedPrefsInstance.setEmail(email) }
                        userId?.let { sharedPrefsInstance.setUserId(userId) }
                        updateLayout()
                    }
                })

            }

            onSignUp = View.OnClickListener {

                Utils.closeTheKeyBoard(requireView())

                if (!checkInputValidity(null, null, null)) {
                    Utils.showSnackBarForFailure(view, "Please enter valid Input in all fields")
                    return@OnClickListener
                }

                val firstName = firstNameInputLayout.editText?.text.toString()
                val lastName = lastNameInputLayout.editText?.text.toString()
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                val signUpRequest = userApi.signup(SignupRequest(firstName=firstName,
                    lastName = lastName, email = email, password = password))

                val signingupView = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Please Wait")
                    .setMessage("Signing up..")
                    .setNegativeButton( "Cancel", object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            signUpRequest.cancel()
                        }
                    }).show()

                signUpRequest
                    .enqueue(object: Callback<MessageResponse>{

                        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                            signingupView.dismiss()
                            Utils.showSnackBarForFailure(view, requireContext()
                                .getString(R.string.error_communicating_with_server))
                        }

                        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                            signingupView.dismiss()
                            if (response.code() == 200) {
                                response.body()!!.message
                                Utils.showSnackBarForSuccess(view, requireContext().getString(R.string.sign_up_successful))
                                setupViewForSignIn()
                            } else {
                                val failureResponse = GsonBuilder().create().fromJson(response.errorBody()!!.string(),
                                    MessageResponse::class.java)
                                Utils.showSnackBarForFailure(view, failureResponse.message)
                            }
                        }
                    })
            }

            signOutButton.setOnClickListener {
                sharedPrefsInstance.clearAll()
                updateLayout()
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateLayout()
    }
    
    override fun onResume() {
        super.onResume()
    }

    fun showOrHideAddItem(showOrHide : ShowAddButton) {
        showAddButtonImpl = showOrHide
    }

    // Private helper methods
    /**
     * Pass in null if you want to check the input validity of all the fields
     * Otherwise pass in the particular field and its validity to color the box Red if error
     */
    private fun checkInputValidity(showErrorOnLayout : Boolean?, inputLayout: TextInputLayout?, errorMessage : String?) : Boolean {

        if (showErrorOnLayout != null && inputLayout != null) {
            var errorMessageToast = "Invalid "+ inputLayout.hint
            if (showErrorOnLayout) {
                inputLayout.setBoxColor(resources.getColor(R.color.errorRedBright, null))
                // Utils.showToastForFailure(requireView(), errorMessage)
            } else {
                inputLayout.setBoxColor(resources.getColor(R.color.border_background_color, null))
            }
            return showErrorOnLayout
        }

        triggerInputValidations() // to trigger the invalid input fields

        var isValid = false

        // For sign In, just check email and if the password is not empty
         isValid = inputValidityMap[emailInput.id]!! && passwordInput.text!!.isNotEmpty()

        // if firstName is visible, its a signup view and we also need to check password validity, firstName and lastName
        if (!isSignInLayout()) {
            isValid = isValid && inputValidityMap[passwordInput.id]!! && inputValidityMap[firstNameInput.id]!!
                    && inputValidityMap[lastNameInput.id]!!
        }

        return isValid
    }

    private fun showEditDialog(id : Int) {
        var title = ""
        var value = ""

        when (id) {
            R.id.profile_firstname_edit -> {
                title = requireContext().getString(R.string.first_name)
                value = sharedPrefsInstance.getFirstName()
            }
            R.id.profile_lastname_edit -> {
                title = requireContext().getString(R.string.last_name)
                value = sharedPrefsInstance.getLastName()
            }
            R.id.profile_email_edit -> {
                title = requireContext().getString(R.string.email)
                value = sharedPrefsInstance.getEmail()
            }
        }

        val editView = (this@AccountFragment).layoutInflater.inflate(R.layout.profile_edit_layout, null)
        val editHeader = (editView.findViewById(R.id.profile_edit_input_layout) as TextInputLayout)
        val profileUpdateButton = (editView.findViewById(R.id.profile_update_button) as MaterialButton)
        editHeader.hint = title
        editHeader.editText?.setText(value)

        val editDialog = MaterialAlertDialogBuilder(requireContext()).create()
        editDialog.setView(editView)
        editDialog.show()

        profileUpdateButton.setOnClickListener {
            updateProfileAttribute(title, editHeader.editText?.text.toString())
            editDialog.dismiss()
        }

    }

    private fun updateProfileAttribute(title : String, value : String) {
        var request = UpdateProfileRequest(id = sharedPrefsInstance.getUserId(), firstName = null, lastName = null,
            email = null, password = null)
        var sharedPreferenceKey = ""
        when (title) {
            getString(R.string.first_name) -> {
                request.firstName = value
                sharedPreferenceKey = sharedPrefsInstance.FIRST_NAME
            }
            getString(R.string.last_name) -> {
                request.lastName = value
                sharedPreferenceKey = sharedPrefsInstance.LAST_NAME
            }
            getString(R.string.email) -> {
                request.email = value
                sharedPreferenceKey = sharedPrefsInstance.EMAIL
            }
            getString(R.string.password) -> {
                request.password = value
                sharedPreferenceKey = sharedPrefsInstance.FIRST_NAME
            }
        }
        userApi.updateProfile(sharedPrefsInstance.getJwtToken(), request).enqueue( object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.code() == 200) {
                    Utils.showSnackBarForSuccess(requireView(), "$title successfully updated")
                    sharedPrefsInstance.putString(sharedPreferenceKey, value)
                    updateEditChanges()
                } else {
                    Utils.showSnackBarForFailure(requireView(), "Error updating $title")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Utils.showSnackBarForFailure(requireView(), "Error updating $title")
            }
        }
        )
    }

    private fun updateEditChanges() {
        profileEmail.text = sharedPrefsInstance.getEmail()
        profileFirstName.text = sharedPrefsInstance.getFirstName()
        profileLastName.text = sharedPrefsInstance.getLastName()
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

    private fun updateLayout() {
        if (isSignedIn()) {
            setupViewForProfile()
        } else {
            profileLayout.visibility = View.GONE
            signInSignOutLayout.visibility = View.VISIBLE
            setupViewForSignIn() // By default show SignIn view
            showAddButtonImpl.showAddButtonInMenu(sharedPrefsInstance.getEmail().isNotEmpty())
        }
    }

    private fun setupViewForProfile() {
        profileLayout.visibility = View.VISIBLE
        signInSignOutLayout.visibility = View.GONE
        showAddButtonImpl.showAddButtonInMenu(sharedPrefsInstance.getEmail().isNotEmpty())
        profileEmail.text = sharedPrefsInstance.getEmail()
        profileFirstName.text = sharedPrefsInstance.getFirstName()
        profileLastName.text = sharedPrefsInstance.getLastName()

        itemsApi.getUserItems(sharedPrefsInstance.getJwtToken(), sharedPrefsInstance.getUserId()).enqueue(object : Callback<List<ItemsResponse>> {
            override fun onResponse(call: Call<List<ItemsResponse>>, response: Response<List<ItemsResponse>>) {
                if (response.code() == 200) {
                    val items = response.body()!!
                    yourItemsView.adapter!!.let { adapter ->  (adapter as ItemsAdapter).setData(items)}
                    noItemsPosted.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    
                }

            }

            override fun onFailure(call: Call<List<ItemsResponse>>, t: Throwable) {
                Utils.showSnackBarForFailure(requireView(), "Error in getting items posted by you")
            }

        })


    }

    private fun triggerInputValidations() {
        if (!isSignInLayout()) {
            firstNameInput.requestFocus()
            firstNameInput.clearFocus()
            lastNameInput.requestFocus()
            lastNameInput.clearFocus()
        }
        emailInput.requestFocus()
        emailInput.clearFocus()
        passwordInput.requestFocus()
        passwordInput.clearFocus()
    }

    private fun isSignedIn() = sharedPrefsInstance.getEmail().isNotEmpty()

    private fun isSignInLayout() = firstNameInputLayout.visibility == View.GONE


    companion object {
        fun newInstance() = AccountFragment()
    }

}