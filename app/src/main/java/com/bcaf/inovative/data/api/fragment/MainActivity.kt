package com.bcaf.inovative.data.api.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.response.BaseResponse
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.databinding.FragmentMainActivityBinding
import com.bcaf.inovative.utils.SessionManager
import com.bcaf.inovative.viewmodel.LoginViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainActivity : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    private lateinit var binding: FragmentMainActivityBinding
    private val viewModel by viewModels<LoginViewModel>()

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
        binding = FragmentMainActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = SessionManager.getToken(requireContext())

        if (!token.isNullOrBlank()) {
            navigateToHome()
        }

        viewModel.loginResult.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
                    showLoading()
                }

                is BaseResponse.Success -> {
                    stopLoading()
                    processLogin(it.data)
                }

                is BaseResponse.Error -> {
                    processError(it.msg)
                }
                else -> {
                    stopLoading()
                }
            }
        }
        // Move the rest of the logic from onCreate to here
        // ...

        binding.btnLogin.setOnClickListener {
            doLogin()
        }

        binding.btnRegister.setOnClickListener {
            doSignup()
        }

    }
    private fun navigateToHome() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frmFragmentRoot, HomeActivity.newInstance("", ""))
            .commit()

    }

    private fun doLogin() {
        val email = binding.txtInputEmail.text.toString()
        val pwd = binding.txtPass.text.toString()
        viewModel.loginUser(email = email, pwd = pwd)
    }

    private fun doSignup() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frmFragmentRoot, RegisterActivity.newInstance("add", ""))
            .commit()
    }

    private fun showLoading() {
        binding.prgbar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.prgbar.visibility = View.GONE
    }

    private fun processLogin(data: LoginResponse?) {
        showToast("Success:" + data?.message)
        if (!data?.data!!.token.isNullOrEmpty()) {
            data?.data!!.token?.let { SessionManager.saveAuthToken(requireContext(), it) }
            if (!data?.data?.id.isNullOrEmpty()) {
                // Save user ID to SessionManager
                data?.data?.id?.let { SessionManager.saveId(requireContext(), it)
                    Log.d("SessionManager", "ID tersimpan: $it")}
            }
            navigateToHome()
        }


    }

    private fun processError(msg: String?) {
        showToast("Error:" + msg)
    }

    // Replace showToast with your actual showToast implementation
    private fun showToast(msg: String) {

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}