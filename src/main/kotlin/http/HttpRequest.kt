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
    val headers = HttpHeader()
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
                headers.set(headerInfo[0], headerInfo[1])
                line = bufferReader.readLine()
            }
        }
    }

    fun getParam(field: String): String {
        return params[field] ?: throw RuntimeException("param $field does not exist")
    }

    fun <T> readJsonBody(type: Class<T>): T {
        logger.debug("reading data")
        check(headers.get("Content-Type") != null) { "Header Error : Content-Type field is empty" }
        checkNotNull(headers.get("Content-Length")) { "Header Error : Content-Length field is empty" }
        check(headers.get("Content-Type") == "application/json") { "Header Error : Content-Type is not application/json" }
        val bodyString = CharArray(headers.get("Content-Length")!!.toInt())
        bufferReader.read(bodyString, 0, headers.get("Content-Length")!!.toInt())
        return mapper.readValue(String(bodyString), type)
    }

    fun setCookieTest(sessionId: String) {
        val resultDTO = mapper.writeValueAsBytes(ResultDTO(result = true, data = "성공"))
        val dataOutputStream = DataOutputStream(outputStream)
        dataOutputStream.writeBytes("HTTP/1.1 200 OK \r\n")
        dataOutputStream.writeBytes("Set-Cookie: sessionId=$sessionId; Path=/; Max-Age=120; \r\n")
        dataOutputStream.writeBytes("Content-Type: application/json;charset=utf-8\r\n")
        dataOutputStream.writeBytes("Content-Length: ${resultDTO.size}\r\n")
        dataOutputStream.writeBytes("\r\n")
        dataOutputStream.write(resultDTO, 0, resultDTO.size)
    }
}

data class ResultDTO(
    val result: Boolean = true,
    val data: Any? = null,
)
