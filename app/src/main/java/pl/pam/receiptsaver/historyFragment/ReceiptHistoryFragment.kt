package pl.pam.receiptsaver.historyFragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.databinding.FragmentReceiptHistoryBinding
import pl.pam.receiptsaver.dto.ReceiptInfoItem
import pl.pam.receiptsaver.receiptDetailsFragment.ReceiptDetailsActivity

class ReceiptHistoryFragment : Fragment(), HistoryListAdapter.OnItemClickListener {

    private val db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://pam-receipt-app-default-rtdb.europe-west1.firebasedatabase.app/")
    private val databaseRef: DatabaseReference = db.reference.child("Receipts");
    private var resultList: List<ReceiptInfoItem> = ArrayList()

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
        resultList = createReceiptsList()
        adapter = HistoryListAdapter(
            resultList as ArrayList<ReceiptInfoItem>,
            resultList as ArrayList<ReceiptInfoItem>,
            this
        )

        binding.historyRecycle.adapter = adapter

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createReceiptsList(): ArrayList<ReceiptInfoItem> {
        var itemList = ArrayList<ReceiptInfoItem>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val receiptItem: ReceiptInfoItem = it.getValue(ReceiptInfoItem::class.java)!!
                    itemList.add(receiptItem)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return itemList
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.searchView)
        val searchView = menuItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onItemClick(position: Int) {
        val clickedItem: ReceiptInfoItem = resultList[position]
        startActivity(
            Intent(requireContext(), ReceiptDetailsActivity::class.java).putExtra(
                "data",
                clickedItem
            )
        )
    }
}