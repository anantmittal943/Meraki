package com.anantmittal.meraki

const val refUrl = "https://meraki-1b27e-default-rtdb.europe-west1.firebasedatabase.app/"

data class AppUserInfo(val email: String, val firstName: String, val lastName: String) {
    constructor() : this("", "", "")
}