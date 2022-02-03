package com.joyfulDonkey.headlessreminder.delegate.files

import android.net.Uri

interface WriteFileDelegate {
    fun appendToFile(uri: Uri, content: String)
}