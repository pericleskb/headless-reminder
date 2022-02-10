package com.joyfulDonkey.headlessreminder.delegates.files

import android.net.Uri

interface WriteFileDelegate {
    fun appendToFile(uri: Uri, content: String)
}