package edu.festival.deichkind.util

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class FormDataHelper {

    fun multipartRequest(
        urlTo: String,
        params: Map<String, String>,
        filepath: String,
        filefield: String,
        fileMimeType: String
    ): String {
        var connection: HttpURLConnection? = null
        var outputStream: DataOutputStream? = null
        var inputStream: InputStream? = null

        val twoHyphens = "--"
        val boundary = "*****" + java.lang.Long.toString(System.currentTimeMillis()) + "*****"
        val lineEnd = "\r\n"

        var result = ""

        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 1 * 1024 * 1024

        val q = filepath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val idx = q.size - 1

        try {
            val file = File(filepath)
            val fileInputStream = FileInputStream(file)

            val url = URL(urlTo)
            connection = url.openConnection() as HttpURLConnection

            connection.doInput = true
            connection.doOutput = true
            connection.useCaches = false

            connection.requestMethod = "POST"
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0")
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connection.setRequestProperty("Authorization", "Bearer " + SessionManager.getInstance(null).session?.authToken)

            outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(twoHyphens + boundary + lineEnd)
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd)
            outputStream.writeBytes("Content-Type: $fileMimeType$lineEnd")
            outputStream.writeBytes("Content-Transfer-Encoding: binary$lineEnd")

            outputStream.writeBytes(lineEnd)

            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            buffer = ByteArray(bufferSize)

            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize)
                bytesAvailable = fileInputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            }

            outputStream.writeBytes(lineEnd)

            // Upload POST Data
            val keys = params.keys.iterator()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = params[key]

                outputStream.writeBytes(twoHyphens + boundary + lineEnd)
                outputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"$lineEnd")
                outputStream.writeBytes("Content-Type: text/plain$lineEnd")
                outputStream.writeBytes(lineEnd)
                outputStream.writeBytes(value)
                outputStream.writeBytes(lineEnd)
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)


            if (200 != connection.responseCode) {
                throw Exception()
            }

            inputStream = connection.inputStream

            result = this.convertStreamToString(inputStream)

            fileInputStream.close()
            inputStream!!.close()
            outputStream.flush()
            outputStream.close()

            return result
        } catch (e: Exception) {
            throw e
        }
    }

    private fun convertStreamToString(`is`: InputStream?): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()

        var line: String? = null
        try {
            line = reader.readLine()
            while (line != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return sb.toString()
    }

}