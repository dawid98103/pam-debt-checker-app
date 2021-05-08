package pl.pam.receiptsaver.receiptDetailsFragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_receipt_details.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.dto.ReceiptInfoItem
import pl.pam.receiptsaver.utils.DateFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReceiptDetailsActivity : AppCompatActivity() {

    private var receiptDetails: ReceiptInfoItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_details)

        //Deserializacja przekazanych danych
        receiptDetails = intent.getSerializableExtra("data") as ReceiptInfoItem

        Picasso.get().load(receiptDetails!!.receiptImage).into(receipt_details_image)
        receipt_details_name.text = receiptDetails!!.name
        receipt_details_shop_name.text = receiptDetails!!.shopName
        receipt_details_price.text = "${receiptDetails!!.price} zł"
        receipt_details_date.text = DateFormatter.getFormattedDateFromTs(receiptDetails!!.creationDateTime)
    }
}