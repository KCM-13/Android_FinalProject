package com.example.finalproject.util

import android.content.Context
import com.example.finalproject.model.InstitutionData
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvUtil {

    fun readCsv(context: Context, fileName: String): List<InstitutionData> {
        val result = mutableListOf<InstitutionData>()

        try {
            val charset = if (fileName == "jongno_facility.csv") "UTF-8" else "EUC-KR"
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream, charset))

            reader.readLine() // 헤더 건너뜀

            reader.forEachLine { line ->
                val tokens = splitCsvLine(line)

                val (nameIndex, phoneIndex, locationIndex) = when (fileName) {
                    "gangbuk_care.csv" -> Triple(1, 6, 5)
                    "gangbuk_facility.csv" -> Triple(2, 4, 5)
                    "jongno_care.csv" -> Triple(1, 3, 2)
                    "jongno_facility.csv" -> Triple(2, -1, 3)
                    else -> Triple(0, -1, -1)
                }

                if (tokens.size > nameIndex && tokens.size > locationIndex) {
                    val name = tokens[nameIndex].trim()
                    val phone = if (phoneIndex != -1 && tokens.size > phoneIndex) tokens[phoneIndex].trim() else ""
                    val location = tokens[locationIndex].trim()

                    result.add(InstitutionData(name, phone, location))
                }
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    // 🔸 쉼표가 큰따옴표 내부에 있을 경우 split 방지용 정규식
    private fun splitCsvLine(line: String): List<String> {
        val regex = Regex(""",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)""")
        return line.split(regex).map { it.trim().trim('"') }
    }
}
