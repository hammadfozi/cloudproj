package com.rukus.model

import java.io.Serializable

enum class UserProfileType(userProfileType: String) : Serializable {
    USER("USER"),
    ADMIN("ADMIN");

    var userProfileType: String
        internal set

    init {
        this.userProfileType = userProfileType
    }

}
