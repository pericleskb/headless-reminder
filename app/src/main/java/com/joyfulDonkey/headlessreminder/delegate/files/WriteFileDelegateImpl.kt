package com.joyfulDonkey.headlessreminder.delegate.files

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class WriteFileDelegateImpl(
    private val context: Context
): WriteFileDelegate {

    override fun appendToFile(uri: Uri, content: String) {
        try {
            context.applicationContext.contentResolver.openFileDescriptor(Uri.fromFile(File(uri.path)), "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                    it.write(
                        content.toByteArray()
                    )
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}