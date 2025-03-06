package com.vdjoseluis.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class Service(
    @DocumentId val id: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val refCustomer: DocumentReference? = null,
    val refOperator: DocumentReference? = null,
    val status: String = "",
    val type: String = ""
)
