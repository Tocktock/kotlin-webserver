package http

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.io.*


data class HttpRequest(
    val inputStream: InputStream,
    val outputStream: OutputStream
) {
    var path = ""

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper()
    private val headers = mutableMapOf<String, String>()
    private var method = ""
    private var httpVersion = ""
    private var bufferReader: BufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

    init {
        kotlin.run {
            var line = bufferReader.readLine() ?: return@run
            val requestMeta = line.split(" ")
            method = requestMeta[0]
            path = requestMeta[1].split("/")[1]
            httpVersion = requestMeta[2]
            line = bufferReader.readLine()
            while (!line.isNullOrBlank()) {
                logger.debug(line)
                val headerInfo = line.split(": ")
                check(headerInfo.size == 2) { "Header Error: header: content 형식으로 요청해주세요." }
                headers[headerInfo[0]] = headerInfo[1]
                line = bufferReader.readLine()
            }
        }
    }

    fun readQueryString(): Map<String, String> {
        val queryStringsMap = mutableMapOf<String, String>()
        path.split("?")[1].split("&").map {
            val info = it.split("=")
            queryStringsMap[info[0]] = info[1]
        }
        return queryStringsMap
    }

    fun <T> readJsonBody(type: Class<T>): T? {
        logger.debug("reading data")
        check(headers["Content-Type"] != null) { "Header Error : Content-Type field is empty" }
        checkNotNull(headers["Content-Length"]) { "Header Error : Content-Length field is empty" }
        check(headers["Content-Type"] == "application/json") { "Header Error : Content-Type is not application/json" }
        val bodyString = CharArray(headers["Content-Length"]!!.toInt())
        bufferReader.read(bodyString, 0, headers["Content-Length"]!!.toInt())
        return mapper.readValue(String(bodyString), type)
    }

    fun writeBody(result: Boolean, data: Any) {
        val resultDTO = mapper.writeValueAsBytes(ResultDTO(result = result, data = data))
        val dataOutputStream = DataOutputStream(outputStream)
        dataOutputStream.writeBytes("HTTP/1.1 200 OK \r\n")
        dataOutputStream.writeBytes("Content-Type: application/json;charset=utf-8\r\n")
        dataOutputStream.writeBytes("Content-Length: ${resultDTO.size}\r\n")
        dataOutputStream.writeBytes("\r\n")
        dataOutputStream.write(resultDTO, 0, resultDTO.size)
    }
}

data class ResultDTO(
    val result: Boolean,
    val data: Any?,
)
