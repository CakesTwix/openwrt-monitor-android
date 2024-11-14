package com.yhpgi.openwrtmonitor.domain.model


import com.google.gson.annotations.SerializedName

data class ApiResponse (

    @SerializedName("id"     ) var id     : String? = null, // null
    @SerializedName("result" ) var result : String? = null, // Result as string
    @SerializedName("error"  ) var error  : String? = null  // null

)

data class ResultExec(
    @SerializedName("kernel"      ) var kernel     : String?  = null,
    @SerializedName("hostname"    ) var hostname   : String?  = null,
    @SerializedName("system"      ) var system     : String?  = null,
    @SerializedName("model"       ) var model      : String?  = null,
    @SerializedName("board_name"  ) var boardName  : String?  = null,
    @SerializedName("rootfs_type" ) var rootfsType : String?  = null,
    @SerializedName("release"     ) var release    : ReleaseApi? = ReleaseApi()

)

data class ReleaseApi (

    @SerializedName("distribution" ) var distribution : String? = null,
    @SerializedName("version"      ) var version      : String? = null,
    @SerializedName("revision"     ) var revision     : String? = null,
    @SerializedName("target"       ) var target       : String? = null,
    @SerializedName("description"  ) var description  : String? = null

)