package com.bcaf.inovative.data.api.request

import com.google.gson.annotations.SerializedName


    data class Reply(

        @SerializedName("idUser") val idUser: String,
        @SerializedName("comment") val comment: String,



    )
