package com.bcaf.inovative.data.api.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.inovative.R
import com.bcaf.inovative.adapter.RecyclerViewAdapter
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.User2
import com.bcaf.inovative.utils.SessionManager
import com.bcaf.inovative.utils.SessionManager.clearData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeActivity : Fragment(), CoroutineScope {
    private lateinit var sessionManager: SessionManager
    private val BASE_URL = "https://64d1-103-171-163-131.ngrok-free.app"
    private lateinit var apiService: UserApi
    lateinit var progressBar: ProgressBar

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var job: Job

    private lateinit var likeCountTextView: TextView
    private var likeCount = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Hentikan semua pekerjaan ketika fragment dihancurkan
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txtToken: TextView = view.findViewById(R.id.txtToken)

        txtToken.text = SessionManager.getToken(requireContext())
        val btnLogout: Button = view.findViewById(R.id.btn_logout)
        val btnPost: Button = view.findViewById(R.id.btn_post)
        progressBar = view.findViewById(R.id.progressBar)


        btnLogout.setOnClickListener {
            launch {
                clearData(requireContext())
                val loginFragment = MainActivity()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frmFragmentRoot, loginFragment)
                    .commit()

            }
        }
        btnPost.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frmFragmentRoot, PostActivity.newInstance("add", ""))
                .commit()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Inisialisasi ApiService
        apiService = retrofit.create(UserApi::class.java)
        val token = txtToken.text.toString()

        // Panggil fungsi untuk mendapatkan semua data
        // Panggil fungsi untuk mendapatkan semua data
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response: Response<GetAllPost> = apiService.getAllPost("Bearer $token")
                recyclerView = view.findViewById(R.id.recyclerView)

                if (response.isSuccessful) {
                    val getAllPost: GetAllPost? = response.body()
                    Log.d("YourFragment", "Response: ${getAllPost?.toString()}")
                    getAllPost?.let {
                        val dataItems: List<DataItem> = it.data?.filterNotNull() ?: emptyList()
                        val dataUser: List<DataItem> = it.data?.filterNotNull() ?: emptyList()



                        adapter = RecyclerViewAdapter(dataItems)
                        requireActivity().runOnUiThread {
                            // Set up the RecyclerView and adapter
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            recyclerView.adapter = adapter
                        }
                    }

                } else {
                    Log.e("YourFragment", "Error: ${response.message()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("YourFragment", "Error body: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("YourFragment", "Exception: ${e.message}")
            }
        }

    }
    private fun updateLikeCount() {
        likeCountTextView.text = likeCount.toString()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    }



