package com.anantmittal.meraki

import android.net.Uri
import java.io.Serializable

data class OwnerData(val uri: Uri, val ownerUserName: String, val ownerProfileUrl: String): Serializable
