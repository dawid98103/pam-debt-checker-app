package pl.pam.receiptsaver.firebasedb

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import pl.pam.receiptsaver.R
import pl.pam.receiptsaver.SaveReceiptFragment
import java.util.*

class FirebaseStorageManager {
    private val TAG = "FirebaseStorageManager"
    private val mStorageRef = FirebaseStorage.getInstance().reference
    private lateinit var mProgressDialog: ProgressDialog

    fun uploadImage(mContext: Context, imageURI: Uri) {
        val fileName: String = UUID.randomUUID().toString()

        mProgressDialog = ProgressDialog(mContext)
        mProgressDialog.show()
        mProgressDialog.setContentView(R.layout.custom_dialog)
        mProgressDialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )
        val uploadTask = mStorageRef.child("receipts/${fileName}.png").putFile(imageURI)
        uploadTask.addOnSuccessListener {
            Log.e(TAG, "Obraz wysłany!")
            val downloadURLTask = mStorageRef.child("receipts/${fileName}.png").downloadUrl
            downloadURLTask.addOnSuccessListener {
                SaveReceiptFragment.imgUriToAssign = it.toString()
                mProgressDialog.dismiss()
            }.addOnFailureListener {
                mProgressDialog.dismiss()
            }
            mProgressDialog.dismiss()
        }.addOnFailureListener {
            Log.e(TAG, "Wystąpił błąd!")
            mProgressDialog.dismiss()
        }
    }
}