package pl.pam.receiptsaver

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class FirebaseStorageManager {
    private val TAG = "FirebaseStorageManager"
    private val mStorageRef = FirebaseStorage.getInstance().reference
    private lateinit var mProgressDialog: ProgressDialog

    private var savedImgUri: String = ""

    fun uploadImage(mContext: Context, imageURI: Uri): String {
        val fileName: String = UUID.randomUUID().toString()

        mProgressDialog = ProgressDialog(mContext)
        mProgressDialog.show()
        val uploadTask = mStorageRef.child("receipts/${fileName}.png").putFile(imageURI)
        uploadTask.addOnSuccessListener {
            Log.e(TAG, "Obraz wysłany!")
            val downloadURLTask = mStorageRef.child("receipts/${fileName}.png").downloadUrl
            downloadURLTask.addOnSuccessListener {
                savedImgUri = it.toString()
                mProgressDialog.dismiss()
            }.addOnFailureListener {
                mProgressDialog.dismiss()
            }
            mProgressDialog.dismiss()
        }.addOnFailureListener {
            Log.e(TAG, "Wystąpił błąd!")
            mProgressDialog.dismiss()
        }
        return savedImgUri
    }
}