package com.anantmittal.meraki.data_modals

import java.io.Serializable

data class OwnerData(
	val uri: String,
	val ownerUserName: String,
	val ownerProfileUrl: String
) : Serializable
