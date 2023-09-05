package com.moni.location

import android.content.Context
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStreamReader

class FileHandler(private val context: Context) {
    private val fileName = "location.txt"

    //Function to read text from te file
    fun readText(): String {
        return try {
            val fileInputStream = context.openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var text: String?
            while (bufferedReader.readLine().also { text = it } != null) {
                stringBuilder.append(text)
            }
            stringBuilder.toString()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun writeText(inputData: String) {
        try {
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(inputData.toByteArray())
            fileOutputStream.close() // close the stream after writing
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // -----------------
    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    fun saveToFileInExternalStorage(fileName: String, content: List<String>): Boolean {
        if (isExternalStorageWritable()) {
//            val externalDir = Environment.getExternalStorageDirectory().toString()
            val file = File(context.getExternalFilesDir(null), fileName)

            return try {
                val fileOutputStream = FileOutputStream(file)
                for (dateContent in content) {
                    fileOutputStream.write(dateContent.toByteArray())
                }
                fileOutputStream.close()
                true // File saved successfully
            } catch (e: Exception) {
                e.printStackTrace()
                false // Error while saving file
            }
        } else {
            return false // External storage not writable
        }
    }
}