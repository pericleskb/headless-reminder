package com.joyfulDonkey.headlessreminder.components.files.delegates

import android.content.Context
import android.net.Uri
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class WriteFileDelegate(
    private val context: Context
) {

    fun appendToFile(uri: Uri, content: String) {
        try {
            context.applicationContext.contentResolver.openFileDescriptor(uri, "wa")?.use { fileDescriptor ->
                FileOutputStream(fileDescriptor.fileDescriptor).use { fos ->
                    DataOutputStream(fos).use {
                        it.write(
                            content.toByteArray()
                        )
                        it.flush()
                        it.close()
                    }
                    fos.close()
                }
                fileDescriptor.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}