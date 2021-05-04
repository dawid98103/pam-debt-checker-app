package pl.pam.receiptsaver.historyFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.history_list_item.view.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.dto.ReceiptInfoItem
import pl.pam.receiptsaver.utils.DateFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HistoryListAdapter internal constructor(
    private var receiptInfoResults: ArrayList<ReceiptInfoItem>,
    private var receiptInfoFilterResults: ArrayList<ReceiptInfoItem>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.history_list_item,
            parent, false
        )

        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = receiptInfoResults[position]

        Picasso.get().load(currentItem.receiptImage).into(holder.imageView)
        holder.textHeader.text = currentItem.shopName
        holder.textDesc.text = DateFormatter.getFormattedDateFromTs(currentItem.creationDateTime)
        holder.textAmount.text = "${currentItem.price} zł"
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: ImageView = itemView.history_list_item_img
        val textHeader: TextView = itemView.history_list_item_text_1
        val textDesc: TextView = itemView.history_list_item_text_2
        val textAmount: TextView = itemView.history_list_item_text_3

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun getItemCount() = receiptInfoResults.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (p0 == null || p0.isEmpty()) {
                    filterResults.count = receiptInfoFilterResults.size
                    filterResults.values = receiptInfoFilterResults
                } else {
                    val searchChr: String = p0.toString().toLowerCase()
                    val receiptInfoItem = ArrayList<ReceiptInfoItem>()
                    receiptInfoItem.addAll(receiptInfoFilterResults.filter {
                        it.shopName.toLowerCase().contains(searchChr) ||
                                DateFormatter.getFormattedDateFromTs(it.creationDateTime).contains(searchChr)
                    })

                    filterResults.count = receiptInfoItem.size
                    filterResults.values = receiptInfoItem
                }

                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                receiptInfoResults = p1!!.values as ArrayList<ReceiptInfoItem>
                notifyDataSetChanged()
            }
        }
    }
}