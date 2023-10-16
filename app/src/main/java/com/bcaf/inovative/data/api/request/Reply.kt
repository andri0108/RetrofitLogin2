package com.bcaf.inovative.data.api.request

import com.google.gson.annotations.SerializedName


data class Reply(
    @SerializedName("comment") val comment: String,
    @SerializedName("post") val post: Post7,
    @SerializedName("user") val user: User7

)

data class Post7(
    @SerializedName("idPost") val idPost: Int
)

data class User7(
    @SerializedName("idUser") val idUser: Int
)
