package com.blameo.data.responses

import com.google.gson.annotations.SerializedName

class PageableResponse<T> {
    @SerializedName("content")
    var content: T? = null

    @SerializedName("totalPages")
    var totalPages: Int = 0

    @SerializedName("totalElements")
    var totalElements: Int = 0

    @SerializedName("last")
    var last: Boolean = false

    @SerializedName("first")
    var first: Boolean = false

    @SerializedName("numberOfElements")
    var numberOfElements: Int = 0

    @SerializedName("size")
    var size: Int = 0

    @SerializedName("number")
    var number: Int = 0

    @SerializedName("empty")
    var empty: Boolean = false

    @SerializedName("pageable")
    var pageable: Pageable? = null
}