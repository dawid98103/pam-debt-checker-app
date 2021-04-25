package pl.pam.receiptsaver.historyFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.history_list_item.view.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.dto.ReceiptInfoItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryListAdapter(private val list: List<ReceiptInfoItem>) :
    RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.history_list_item,
            parent, false
        )

        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = list[position]

        Picasso.get().load(currentItem.receiptImage).into(holder.imageView)
        holder.textHeader.text = currentItem.shopName
        holder.textDesc.text = getDateTime(currentItem.creationDateTime)
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.history_list_item_img
        val textHeader: TextView = itemView.history_list_item_text_1
        val textDesc: TextView = itemView.history_list_item_text_2
    }

    override fun getItemCount() = list.size

    private fun getDateTime(s: String): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime: LocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(s.toLong()),
            TimeZone.getDefault().toZoneId()
        )
        return dateTime.format(formatter)
    }
}