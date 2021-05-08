package pl.pam.receiptsaver.dto

import java.io.Serializable

data class ReceiptInfoItem(
    var categoryId: String = "",
    var creationDateTime: String = "",
    var name: String = "",
    var price: String = "",
    var receiptImage: String = "",
    var shopName: String = ""
) : Serializable
