package com.bcaf.inovative.data.api.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bcaf.inovative.R
import com.bcaf.inovative.data.api.methods.UserApi
import com.bcaf.inovative.data.api.request.Post
import com.bcaf.inovative.data.api.request.Post2
import com.bcaf.inovative.data.api.response.LoginResponse
import com.bcaf.inovative.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostActivity.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostActivity : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var imgcam : ImageButton
    lateinit var imgpp : ImageView
    private lateinit var txtToken: TextView
    private lateinit var txtId: TextView

    lateinit var progressBar: ProgressBar
    lateinit var bitmap: Bitmap

    private lateinit var userApi: UserApi

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
        return inflater.inflate(R.layout.fragment_post_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtToken = view.findViewById(R.id.txtToken)
        txtToken.text = SessionManager.getToken(requireContext())
        txtId = view.findViewById(R.id.txtId)
        txtId.text = SessionManager.getId(requireContext())


        imgcam = view.findViewById(R.id.imgCam)
        imgpp = view.findViewById(R.id.imgpp)

        //btn openkamera
        imgcam.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent()
        })
        val bitmap = (imgpp.drawable as? BitmapDrawable)?.bitmap
        bitmap?.let {
            val image = encodeImage(it)
            // Lakukan sesuatu dengan encodedImage


        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://0dab-180-252-169-202.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userApi = retrofit.create(UserApi::class.java)
        val id = txtId.text.toString()
        val editTextJudul: EditText = view.findViewById(R.id.editJudul)
        val editTextDeskripsi: EditText = view.findViewById(R.id.editdeskripsi)
        val buttonInsert: Button = view.findViewById(R.id.btnSend)
        buttonInsert.setOnClickListener {

            val nilaiId: Int? = id.toIntOrNull()
            val id: Int = nilaiId!!
            val post1 = Post(id)
            val judulPost = editTextJudul.text.toString()
            val deskripsi = editTextDeskripsi.text.toString()
            val post2 = Post2(judulPost, "kemanaaja.com", deskripsi, 0,
                "kkklk", post1)
            // Memanggil fungsi untuk memasukkan data pengguna
            insertData(post2)


        }
        }

        private fun insertData(post2: Post2) {
            val token = txtToken.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    // Memanggil API untuk memasukkan data pengguna
                    val response: Response<LoginResponse> = userApi.createPost("Bearer $token",post2)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val createPost: LoginResponse? = response.body()
                            // Handle the created user
                            Log.d("Response", "User created: $createPost")

                            // Tampilkan toast jika data berhasil disimpan
                            Toast.makeText(
                                requireContext(),
                                "Data berhasil disimpan",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(requireContext(), PostActivity::class.java)
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
         * @return A new instance of fragment PostActivity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostActivity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureIntent.resolveActivity(requireActivity().packageManager)
            startActivityForResult(takePictureIntent, 1)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==1 && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imgpp.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }
    fun createImageRequestBody(bitmap:Bitmap?): MultipartBody.Part{

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("image", System.currentTimeMillis().toString()+"image.jpg", requestBody)
    }
    private fun encodeImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

}