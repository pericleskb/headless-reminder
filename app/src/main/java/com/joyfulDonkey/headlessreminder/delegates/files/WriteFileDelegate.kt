package com.joyfulDonkey.headlessreminder.delegates.files

import android.content.Context
import android.net.Uri
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class WriteFileDelegate(
    private val context: Context
) {

    fun
            appendToFile(uri: Uri, content: String) {
        try {
            context.applicationContext.contentResolver.openAssetFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
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