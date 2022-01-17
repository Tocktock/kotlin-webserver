package http

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


data class HttpRequest(
    val inStream: InputStream
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    val headers = mutableMapOf<String, String>()
    var method: String
    var path: String
    var httpVersion: String

    init {
        val buffer = BufferedReader(InputStreamReader(inStream, "UTF-8"))
        var line = buffer.readLine()
        val requestMeta = line.split(" ")
        method = requestMeta[0]
        path = requestMeta[1]
        httpVersion = requestMeta[2]
        line = buffer.readLine()
        while (!line.isNullOrBlank()) {
            logger.debug(line)
            val headerInfo = line.split(": ")
            check(headerInfo.size == 2) { "Header Error: header: content 형식으로 요청해주세요." }
            headers[headerInfo[0]] = headerInfo[1]
            line = buffer.readLine()
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

    fun <T> readJsonBody(br: BufferedReader, type: Class<T>): T? {
        logger.debug("reading data")
        check(headers["Content-Type"] != null) { "Header Error : Content-Type field is empty" }
        checkNotNull(headers["Content-Length"]) { "Header Error : Content-Length field is empty" }
        check(headers["Content-Type"] == "application/json") { "Header Error : Content-Type is not application/json" }
        val bodyString = CharArray(headers["Content-Length"]!!.toInt())
        br.read(bodyString, 0, headers["Content-Length"]!!.toInt())
        val mapper = ObjectMapper()
        return mapper.readValue(String(bodyString), type)
    }
}

data class FieldOneFieldTwo(
    val field_one: String? = null,
    val field_two: String? = null,
)