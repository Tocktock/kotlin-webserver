import com.fasterxml.jackson.databind.ObjectMapper
import http.HttpRequest
import org.slf4j.LoggerFactory
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket


class RequestHandler(
    private val socket: Socket
) : Thread() {

    private val logger = LoggerFactory.getLogger(RequestHandler::class.java)
    private val mapper = ObjectMapper()

    override fun run() {
        logger.debug("request arrived from ${socket.inetAddress} : ${socket.port}")
        try {
            val inStream = socket.getInputStream()
            val outStream = socket.getOutputStream()
            val request = HttpRequest(inStream, outStream)
            router[request.path.split("?")[0]]?.invoke(request).let {
                val dos = DataOutputStream(outStream)
                response200Header(dos, request.responseHeader.getAll())
                responseBody(dos, mapper.writeValueAsBytes(it))
                dos.close()
            }
        } catch (error: RuntimeException) {
            error.printStackTrace()
            logger.error("request fail:  ${error.message}")
        }
    }

    private fun response200Header(dos: DataOutputStream, headers: Map<String, String>) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n")
            headers.forEach { (key, value) ->
                dos.writeBytes("$key: $value \r\n")
            }
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

    private fun responseBody(dos: DataOutputStream, body: ByteArray) {
        try {
            dos.writeBytes("Content-Type: application/json;charset=utf-8; \r\n")
            dos.writeBytes("Content-Length: ${body.size} \r\n")
            dos.writeBytes("\r\n")
            dos.write(body, 0, body.size)
            dos.flush()
            logger.debug("flush")
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }
}