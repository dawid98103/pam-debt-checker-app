package pl.pam.receiptsaver.receiptDetailsFragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_receipt_details.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.dto.ReceiptInfoItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReceiptDetailsActivity : AppCompatActivity() {

    private var receiptDetails: ReceiptInfoItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_details)

        receiptDetails = intent.getSerializableExtra("data") as ReceiptInfoItem

        Picasso.get().load(receiptDetails!!.receiptImage).into(receipt_details_image)
        receipt_details_name.text = receiptDetails!!.name
        receipt_details_shop_name.text = receiptDetails!!.shopName
        receipt_details_price.text = "${receiptDetails!!.price} zł"
        receipt_details_date.text = getDateTime(receiptDetails!!.creationDateTime)
    }

    private fun getDateTime(s: String): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime: LocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(s.toLong()),
            TimeZone.getDefault().toZoneId()
        )
        return dateTime.format(formatter)
    }
}