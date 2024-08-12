package com.sotul.noteapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.sotul.noteapp.model.Note
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

fun importFromExcel(inputStream: InputStream, context: Context): List<Note>? {
    val notes = mutableListOf<Note>()
    try {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            val id = row.getCell(0)?.numericCellValue?.toInt() ?: continue
            val title = row.getCell(1)?.stringCellValue ?: continue
            val content = row.getCell(2)?.stringCellValue ?: continue
            val category = row.getCell(3)?.stringCellValue ?: continue
            val color = row.getCell(4)?.numericCellValue?.toInt() ?: -1
            val createdAt = row.getCell(5)?.stringCellValue ?: continue
            val updatedAt = row.getCell(6)?.stringCellValue ?: continue

            val note = Note(
                id = 0,
                title = title,
                content = content,
                category = category,
                color = color,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
            notes.add(note)
        }
        workbook.close()
    } catch (e: Exception) {
        Log.d("Error", e.stackTraceToString())
        Toast.makeText(context, "Failed to import Excel file: ${e.message}", Toast.LENGTH_LONG).show()
        return null
    }

    return notes
}
