package pl.pam.receiptsaver.saveReceiptFragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_save_receipt.*
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.databinding.FragmentSaveReceiptBinding
import pl.pam.receiptsaver.firebasedb.FirebaseStorageManager
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

class SaveReceiptFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    private val CAMERA_PERMISSION_CODE = 101
    private val REQUEST_CAMERA_CODE = 102

    private var categoryToAssign: Int = 0

    companion object {
        var imgUriToAssign: String = ""
    }

    private val db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://pam-receipt-app-default-rtdb.europe-west1.firebasedatabase.app/")
    private val databaseRef: DatabaseReference = db.reference.child("Receipts");

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSaveReceiptBinding>(
            inflater,
            R.layout.fragment_save_receipt, container, false
        )

        binding.spCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                categoryToAssign = id.toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding.dateButton.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
        binding.receiptImage.setOnClickListener {
            askCameraPermissions()
        }
        binding.saveReceiptButton.setOnClickListener {
            val receiptPrice: String = enter_price.editText?.text.toString()
            val receiptName: String = enter_receipt_name.editText?.text.toString()
            val shopName: String = enter_shop_name.editText?.text.toString()
            val receiptMap: HashMap<String, String> = HashMap()

            val timestamp: Timestamp =
                Timestamp.valueOf("$savedYear-$savedMonth-$savedDay $savedHour:$savedMinute:00")

            receiptMap["price"] = receiptPrice
            receiptMap["name"] = receiptName
            receiptMap["shopName"] = shopName
            receiptMap["categoryId"] = categoryToAssign.toString()
            receiptMap["creationDateTime"] = timestamp.time.toString()
            receiptMap["receiptImage"] =
                imgUriToAssign

            databaseRef.push().setValue(receiptMap).addOnSuccessListener {
                Toast.makeText(requireContext(), "Paragon zapisano pomyślnie!", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(SaveReceiptFragmentDirections.actionSaveReceiptFragmentToTitleFragment())
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Błąd połączenia!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        ImagePicker.with(this).crop().cameraOnly().start(REQUEST_CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.size < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(context, "Brak uprawnień do aparatu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        date_text.text = "$savedDay-$savedMonth-$savedYear   $savedHour:$savedMinute"
        date_button.text = "Zmień datę zakupu"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                receipt_image.setImageURI(data!!.data)
                FirebaseStorageManager().uploadImage(requireContext(), data.data!!)
            }
        }
    }
}