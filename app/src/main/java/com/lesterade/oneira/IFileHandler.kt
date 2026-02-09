package com.lesterade.oneira

interface IFileHandler {
    fun readSaveText(): String
    fun deleteSaveFile()
    fun saveExists(): Boolean
    fun writeSave(text: String)
}