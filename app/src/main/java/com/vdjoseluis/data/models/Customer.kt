package com.vdjoseluis.data.models

data class Customer(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val addressAdditional: String = "",
    val phone: String = "",
) {
    val name: String
        get() = "$firstName $lastName"
}
