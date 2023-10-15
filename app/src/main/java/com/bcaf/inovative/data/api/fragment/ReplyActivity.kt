package com.bcaf.inovative.data.api.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bcaf.inovative.R
import com.bcaf.inovative.adapter.RecyclerViewAdapter
import com.bcaf.inovative.adapter.RecyclerViewAdapter2
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.DataItem
import com.bcaf.inovative.data.api.request.GetAllPost
import com.bcaf.inovative.data.api.request.GetAllPostId
import com.bcaf.inovative.data.api.request.Reply
import com.bcaf.inovative.data.api.request.User
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReplyActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReplyActivity : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var postItem: DataItem? = null
    private lateinit var userApi: UserApi
    private lateinit var recyclerView2: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter2
    lateinit var sendButton : ImageButton
    private lateinit var nameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            postItem = it.getSerializable("postItem") as DataItem

        }
    }
    private var allPostId: GetAllPostId? = null

    fun setAllPostId(getAllPostId: GetAllPostId) {
        allPostId = getAllPostId
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reply_activity, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val txtToken: TextView = view.findViewById(R.id.txtToken)
        txtToken.text = SessionManager.getToken(requireContext())

        val txtId: TextView = view.findViewById(R.id.txtId)
        txtId.text = SessionManager.getId(requireContext())
        recyclerView2 = view.findViewById(R.id.recyclerView2)


        val dataItem: DataItem = postItem!!

        adapter = RecyclerViewAdapter2(dataItem)
        // Set up the RecyclerView and adapter
        Log.i("datanya", dataItem.toString())
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        recyclerView2.adapter = adapter
        nameTextView = view.findViewById(R.id.txtNama)
        sendButton = view.findViewById(R.id.sendButton)
        val editTextKomen: EditText = view.findViewById(R.id.messageEditText)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://0dab-180-252-169-202.ngrok-free.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        sendButton.setOnClickListener {
            val token = txtToken.text.toString()
            val id = txtId.text.toString()
            val komen = editTextKomen.text.toString()

            val reply = Reply(id,komen)

            // Memanggil fungsi untuk memasukkan data pengguna
            insertData(reply)
        }
    }
    private fun insertData(reply: Reply) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Memanggil API untuk memasukkan data pengguna
                val response: Response<GetAllPost> = userApi.createReply(reply)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdReply: GetAllPost? = response.body()
                        // Handle the created user
                        Log.d("Response", "User created: $createdReply")

                        // Tampilkan toast jika data berhasil disimpan
                        Toast.makeText(
                            requireContext(),
                            "Data berhasil disimpan",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Handle unsuccessful response
                        Log.e("API_ERROR", "Unsuccessful response: ${response.code()}")
                        Toast.makeText(
                            requireContext(),
                            "Gagal menyimpan data. Status code: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                // Handle network errors or other exceptions
                Log.e("API_ERROR", "Request failed: ${e.message}", e)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReplyActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReplyActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}