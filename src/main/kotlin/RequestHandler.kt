import http.HttpRequest
import org.slf4j.LoggerFactory
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket


class RequestHandler(
    private val socket: Socket
) : Thread() {

    private val logger = LoggerFactory.getLogger(RequestHandler::class.java)

    override fun run() {
        logger.debug("request arrived from ${socket.inetAddress} : ${socket.port}")
        try {
            val inStream = socket.getInputStream()
            val outStream = socket.getOutputStream()
            try {
                val request = HttpRequest(inStream, outStream)
                router[request.path.split("?")[0]]?.invoke(request)
            } catch (error: Exception) {
                error.printStackTrace()
                logger.error("request initialize fail:  ${error.message}")
            }

        } catch (e: RuntimeException) {
            e.printStackTrace()
            logger.error("request handle failed: ${e.message}")
        }
    }

    private fun response200Header(dos: DataOutputStream, lengthOfBodyContent: Int) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n")
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n")
            dos.writeBytes("Content-Length: $lengthOfBodyContent\r\n")
            dos.writeBytes("\r\n")
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

    private fun responseBody(dos: DataOutputStream, body: ByteArray) {
        try {
            dos.write(body, 0, body.size)
            dos.flush()
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

}