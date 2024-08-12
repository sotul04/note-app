package com.sotul.noteapp.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.sotul.noteapp.model.Note
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun exportToXLS(notes: List<Note>, context: Context) {
    val workbook = HSSFWorkbook()
    val sheet = workbook.createSheet("Notes")

    // Header row
    val header: Row = sheet.createRow(0)
    header.createCell(0).setCellValue("ID")
    header.createCell(1).setCellValue("Title")
    header.createCell(2).setCellValue("Content")
    header.createCell(3).setCellValue("Category")
    header.createCell(4).setCellValue("Color")
    header.createCell(5).setCellValue("Created At")
    header.createCell(6).setCellValue("Updated At")

    // Populate rows with notes
    notes.forEachIndexed { index, note ->
        val row: Row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(note.id.toDouble())
        row.createCell(1).setCellValue(note.title)
        row.createCell(2).setCellValue(note.content)
        row.createCell(3).setCellValue(note.category)
        row.createCell(4).setCellValue(note.color.toDouble())
        row.createCell(5).setCellValue(note.createdAt)
        row.createCell(6).setCellValue(note.updatedAt)
    }

    saveExcelFile(context, workbook, "Notes.xls")
}

fun exportToXLSX(notes: List<Note>, context: Context) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Notes")

    // Header row
    val header: Row = sheet.createRow(0)
    header.createCell(0).setCellValue("ID")
    header.createCell(1).setCellValue("Title")
    header.createCell(2).setCellValue("Content")
    header.createCell(3).setCellValue("Category")
    header.createCell(4).setCellValue("Color")
    header.createCell(5).setCellValue("Created At")
    header.createCell(6).setCellValue("Updated At")

    // Populate rows with notes
    notes.forEachIndexed { index, note ->
        val row: Row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(note.id.toDouble())
        row.createCell(1).setCellValue(note.title)
        row.createCell(2).setCellValue(note.content)
        row.createCell(3).setCellValue(note.category)
        row.createCell(4).setCellValue(note.color.toDouble())
        row.createCell(5).setCellValue(note.createdAt)
        row.createCell(6).setCellValue(note.updatedAt)
    }

    saveExcelFile(context, workbook, "Notes.xlsx")
}

private fun saveExcelFile(context: Context, workbook: org.apache.poi.ss.usermodel.Workbook, fileName: String) {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)
    try {
        FileOutputStream(file).use { outputStream ->
            workbook.write(outputStream)
            outputStream.flush()
            Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        Toast.makeText(context, "Failed to export $fileName: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        workbook.close()
    }
}