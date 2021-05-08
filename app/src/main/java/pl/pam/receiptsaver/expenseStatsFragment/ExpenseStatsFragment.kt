package pl.pam.receiptsaver.expenseStatsFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.android.synthetic.main.fragment_expense_stats.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.constants.ExpenseCategory
import pl.pam.receiptsaver.databinding.FragmentExpenseStatsBinding
import pl.pam.receiptsaver.dto.ReceiptInfoItem
import pl.pam.receiptsaver.utils.DateFormatter
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ExpenseStatsFragment : Fragment() {
    //Instancja bazy danych Firebase
    private val db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://pam-receipt-app-default-rtdb.europe-west1.firebasedatabase.app/")

    //Referencja do odpowiedniego dokumentu bazy danych
    private val databaseRef: DatabaseReference = db.reference.child("Receipts");
    private var resultList: List<ReceiptInfoItem> = ArrayList()
    private var listToRender: ArrayList<String> = ArrayList()

    //Adapter wykorzystywany przy renderowaniu listy wydatków
    private lateinit var adapter: ArrayAdapter<String>

    private var selectedYear = 0;
    private var selectedMonth = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentExpenseStatsBinding>(
            inflater,
            R.layout.fragment_expense_stats, container, false
        )

        resultList = retrieveDataFromDb()

        binding.dateButton.setOnClickListener {
            val today: Calendar = Calendar.getInstance()
            val builder = MonthPickerDialog.Builder(
                requireContext(),
                { month, year ->
                    run {
                        selectedMonth = month + 1
                        selectedYear = year
                        date_text.text = "${
                            if (selectedMonth > 10) {
                                selectedMonth
                            } else {
                                "0${selectedMonth}"
                            }
                        } - $selectedYear"
                        refreshListData(selectedMonth, selectedYear)
                    }
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH)
            ).also {
                it.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1990)
                    .setActivatedYear(today.get(Calendar.YEAR))
                    .setMaxYear(2030)
                    .setTitle("Wybierz miesiąc i rok")
                    .build()
                    .show()
            }
        }

        val listView: ListView = binding.expenseList

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listToRender)
        listView.adapter = adapter

        return binding.root
    }

    /**
     * Odświeża dane wydatków z uwzględnieniem podanego miesiąca oraz roku
     * @property month miesiąc
     * @property year rok
     */
    private fun refreshListData(month: Int, year: Int) {
        listToRender.clear()

        val ts: Timestamp = Timestamp.valueOf("$year-$month-1 00:00:00")
        val pickedDateTime: LocalDateTime = DateFormatter.getLocalDateTimeFromTs(ts.time)

        val mapResult: Map<String, List<ReceiptInfoItem>> = resultList.filter {
            DateFormatter.getLocalDateTimeFromTs(it.creationDateTime.toLong()).month == pickedDateTime.month &&
                    DateFormatter.getLocalDateTimeFromTs(it.creationDateTime.toLong()).year == pickedDateTime.year
        }.groupBy { it.categoryId }

        mapResult.forEach {
            listToRender.add(
                "${ExpenseCategory.getNameById(it.key)}  ${
                    it.value.map { it.price.toDouble() }.sum()
                } zł"
            )
        }
        adapter.notifyDataSetChanged()
    }

    /**
     * Pobiera dane wydatków z bazy danych realtime database
     */
    private fun retrieveDataFromDb(): ArrayList<ReceiptInfoItem> {
        val itemList = ArrayList<ReceiptInfoItem>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val receiptItem: ReceiptInfoItem = it.getValue(ReceiptInfoItem::class.java)!!
                    itemList.add(receiptItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return itemList
    }
}