package com.lesterade.oneira

import android.content.Context

class AndroidFileHandler(private val context: Context): IFileHandler {
    override fun readSaveText(): String {
        val stream = context.openFileInput("save.json")

        var text = String()
        while(stream.available() != 0)
            text += String(byteArrayOf(stream.read().toByte()))

        stream.close()
        return text
    }

    override fun deleteSaveFile() {
        context.deleteFile("save.json")
    }

    override fun saveExists(): Boolean = context.fileList().contains("save.json")

    override fun writeSave(text: String) {
        val stream = context.openFileOutput("save.json", 0)
        stream.write(text.toByteArray())
        stream.close()
    }
}