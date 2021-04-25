package pl.pam.receiptsaver.historyFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.databinding.FragmentReceiptHistoryBinding
import pl.pam.receiptsaver.dto.ReceiptInfoItem

class ReceiptHistoryFragment : Fragment() {

    private val db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://pam-receipt-app-default-rtdb.europe-west1.firebasedatabase.app/")
    private val databaseRef: DatabaseReference = db.reference.child("Receipts");

    private lateinit var adapter: HistoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentReceiptHistoryBinding>(
            inflater, R.layout.fragment_receipt_history, container, false
        )

        binding.historyRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecycle.setHasFixedSize(true)
        adapter = HistoryListAdapter(createReceiptsList())

        binding.historyRecycle.adapter = adapter

        return binding.root
    }

    private fun createReceiptsList(): List<ReceiptInfoItem> {
        var itemList = ArrayList<ReceiptInfoItem>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val receiptItem: ReceiptInfoItem = it.getValue(ReceiptInfoItem::class.java)!!
                    itemList.add(receiptItem)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
        return itemList
    }
}