package http

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream


data class HttpRequest(
    val inputStream: InputStream,
    val outputStream: OutputStream
) {
    var path = ""
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = ObjectMapper()
    val requestHeader = HttpHeader()
    val responseHeader = HttpHeader()
    private var method = ""
    private var httpVersion = ""
    private var bufferReader: BufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
    private val params = mutableMapOf<String, String>()

    init {
        run {
            var line = bufferReader.readLine() ?: return@run
            val requestMeta = line.split(" ")
            logger.info("meta : $requestMeta")

            method = requestMeta[0]
            val pathInfo = requestMeta[1].split("?")
            path = pathInfo[0]
            if (pathInfo.size > 1) {
                pathInfo[1].split("&").forEach { param ->
                    val kv = param.split("=")
                    params[kv[0]] = kv[1]
                }
            }
            httpVersion = requestMeta[2]

            line = bufferReader.readLine()
            while (!line.isNullOrBlank()) {
                logger.debug(line)
                val headerInfo = line.split(": ")
                check(headerInfo.size == 2) { "Header Error: header: content 형식으로 요청해주세요." }
                requestHeader.set(headerInfo[0], headerInfo[1])
                line = bufferReader.readLine()
            }
        }
    }

    fun getParam(field: String): String {
        return params[field] ?: throw RuntimeException("param $field does not exist")
    }

    fun <T> readJsonBody(type: Class<T>): T {
        logger.debug("reading data")
        check(requestHeader.get("Content-Type") != null) { "Header Error : Content-Type field is empty" }
        checkNotNull(requestHeader.get("Content-Length")) { "Header Error : Content-Length field is empty" }
        check(requestHeader.get("Content-Type") == "application/json") { "Header Error : Content-Type is not application/json" }
        val bodyString = CharArray(requestHeader.get("Content-Length")!!.toInt())
        bufferReader.read(bodyString, 0, requestHeader.get("Content-Length")!!.toInt())
        return mapper.readValue(String(bodyString), type)
    }
}

data class ResultDTO(
    val result: Boolean = true,
    val data: Any? = null,
)
